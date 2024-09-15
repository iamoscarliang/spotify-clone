package com.oscarliang.spotifyclone.feature.category;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.scheduler.Scheduled;
import com.oscarliang.spotifyclone.core.common.scheduler.SchedulerType;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.CategoryRepository;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.model.Category;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.ui.util.LoadMoreState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class CategoryViewModel extends ViewModel {

    private final CategoryRepository categoryRepository;
    private final MusicRepository musicRepository;
    private final Scheduler ioScheduler;

    private final BehaviorSubject<Result<Pair<Category, List<Music>>>> result = BehaviorSubject.create();
    private final BehaviorSubject<LoadMoreState> loadMoreState = BehaviorSubject.createDefault(LoadMoreState.IDLING);

    @VisibleForTesting
    final BehaviorSubject<CategoryQuery> query = BehaviorSubject.create();

    @VisibleForTesting
    final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public CategoryViewModel(
            CategoryRepository categoryRepository,
            MusicRepository musicRepository,
            @Scheduled(SchedulerType.IO) Scheduler ioScheduler
    ) {
        this.categoryRepository = categoryRepository;
        this.musicRepository = musicRepository;
        this.ioScheduler = ioScheduler;
    }

    public Observable<Result<Pair<Category, List<Music>>>> getResult() {
        return result;
    }

    public Observable<LoadMoreState> getLoadMoreState() {
        return loadMoreState;
    }

    public void setQuery(@NonNull CategoryQuery query) {
        if (Objects.equals(this.query.getValue(), query)) {
            return;
        }
        loadCategoryData();
        this.query.onNext(query);
    }

    public void retry() {
        CategoryQuery current = query.getValue();
        if (current != null && !current.isEmpty()) {
            query.onNext(current);
        }
    }

    private void loadCategoryData() {
        disposables.clear();
        disposables.add(query
                .switchMap(query -> {
                    if (query == null || query.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return Single.zip(
                                        categoryRepository.getCategoryById(query.categoryId),
                                        musicRepository.getMusicsByCategoryId(query.categoryId, query.count),
                                        (category, musics) -> Pair.create(category, musics)
                                )
                                .doOnSuccess(data -> loadMoreState
                                        .onNext(data.second.size() < query.count ? LoadMoreState.NO_MORE : LoadMoreState.IDLING))
                                .map(data -> Result.success(data))
                                .onErrorReturn(throwable -> Result.error(throwable.getMessage(), null))
                                .toObservable()
                                .startWithItem(Result.loading());
                    }
                })
                .subscribeOn(ioScheduler)
                .subscribe(result -> this.result.onNext(result))
        );
    }

    public void loadNextPage() {
        LoadMoreState state = loadMoreState.getValue();
        if (state == LoadMoreState.RUNNING || state == LoadMoreState.NO_MORE) {
            return;
        }
        loadMoreState.onNext(LoadMoreState.RUNNING);
        // Unsubscribe the previous page stream, so
        // when retry request, only the last page will
        // be triggered, instant of the whole pages
        disposables.clear();
        disposables.add(query
                .switchMap(query -> musicRepository
                        .getMusicsByCategoryIdNextPage(query.categoryId, query.count)
                        .doOnSuccess(musics -> loadMoreState
                                .onNext(musics.size() < query.count ? LoadMoreState.NO_MORE : LoadMoreState.IDLING))
                        .doOnError(throwable -> loadMoreState.onNext(LoadMoreState.ERROR))
                        .map(data -> Result.success(appendMusic(data)))
                        // Return the old data, so when next page successfully
                        // fetched, the new data can be appended to the old one
                        .onErrorReturn(throwable -> Result.error(throwable.getMessage(), result.getValue().data.second))
                        .toObservable()
                )
                .subscribeOn(ioScheduler)
                .subscribe(result -> {
                    Category current = this.result.getValue().data.first;
                    switch (result.state) {
                        case SUCCESS:
                            this.result.onNext(Result.success(Pair.create(current, result.data)));
                            break;
                        case ERROR:
                            this.result.onNext(Result.error(result.message, Pair.create(current, result.data)));
                            break;
                    }
                })
        );
    }

    private List<Music> appendMusic(List<Music> next) {
        // Create a new list to trigger AsyncListDiffer update
        List<Music> updated = new ArrayList<>();
        List<Music> current = result.getValue().data.second;
        updated.addAll(current);
        updated.addAll(next);
        return updated;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

}