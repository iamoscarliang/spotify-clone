package com.oscarliang.spotifyclone.feature.searchresult;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.scheduler.Scheduled;
import com.oscarliang.spotifyclone.core.common.scheduler.SchedulerType;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.ArtistRepository;
import com.oscarliang.spotifyclone.core.model.Artist;
import com.oscarliang.spotifyclone.core.ui.util.LoadMoreState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ArtistResultViewModel extends ViewModel {

    private final ArtistRepository artistRepository;
    private final Scheduler ioScheduler;

    private final BehaviorSubject<Result<List<Artist>>> result = BehaviorSubject.create();
    private final BehaviorSubject<LoadMoreState> loadMoreState = BehaviorSubject.createDefault(LoadMoreState.IDLING);

    @VisibleForTesting
    final BehaviorSubject<SearchResultQuery> query = BehaviorSubject.create();

    @VisibleForTesting
    final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public ArtistResultViewModel(
            ArtistRepository artistRepository,
            @Scheduled(SchedulerType.IO) Scheduler ioScheduler
    ) {
        this.artistRepository = artistRepository;
        this.ioScheduler = ioScheduler;
    }

    public Observable<Result<List<Artist>>> getResult() {
        return result;
    }

    public Observable<LoadMoreState> getLoadMoreState() {
        return loadMoreState;
    }

    public void setQuery(@NonNull SearchResultQuery query) {
        if (Objects.equals(this.query.getValue(), query)) {
            return;
        }
        loadArtistData();
        this.query.onNext(query);
    }

    public void retry() {
        SearchResultQuery current = query.getValue();
        if (current != null && !current.isEmpty()) {
            query.onNext(current);
        }
    }

    private void loadArtistData() {
        disposables.clear();
        disposables.add(query
                .switchMap(query -> {
                    if (query == null || query.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return artistRepository
                                .search(query.input, query.count)
                                .doOnSuccess(music -> loadMoreState
                                        .onNext(music.size() < query.count ? LoadMoreState.NO_MORE : LoadMoreState.IDLING))
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
                .switchMap(query -> artistRepository
                        .searchNextPage(query.input, query.count)
                        .doOnSuccess(music -> loadMoreState
                                .onNext(music.size() < query.count ? LoadMoreState.NO_MORE : LoadMoreState.IDLING))
                        .doOnError(throwable -> loadMoreState.onNext(LoadMoreState.ERROR))
                        .map(data -> Result.success(appendData(data)))
                        // Return the old data, so when next page successfully
                        // fetched, the new data can be appended to the old one
                        .onErrorReturn(throwable -> Result.error(throwable.getMessage(), result.getValue().data))
                        .toObservable()
                )
                .subscribeOn(ioScheduler)
                .subscribe(result -> this.result.onNext(result))
        );
    }

    private List<Artist> appendData(List<Artist> next) {
        // Create a new list to trigger AsyncListDiffer update
        List<Artist> updated = new ArrayList<>();
        List<Artist> current = result.getValue().data;
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