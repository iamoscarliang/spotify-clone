package com.oscarliang.spotifyclone.feature.searchresult;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.scheduler.Scheduled;
import com.oscarliang.spotifyclone.core.common.scheduler.SchedulerType;
import com.oscarliang.spotifyclone.core.data.repository.RecentSearchRepository;
import com.oscarliang.spotifyclone.core.model.RecentSearch;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class SearchResultViewModel extends ViewModel {

    private final RecentSearchRepository recentSearchRepository;
    private final Scheduler ioScheduler;

    private final BehaviorSubject<String> query = BehaviorSubject.createDefault("");

    @VisibleForTesting
    final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public SearchResultViewModel(
            RecentSearchRepository recentSearchRepository,
            @Scheduled(SchedulerType.IO) Scheduler ioScheduler
    ) {
        this.recentSearchRepository = recentSearchRepository;
        this.ioScheduler = ioScheduler;
    }

    public Observable<String> getQuery() {
        return query;
    }

    public void setQuery(@NonNull String query) {
        if (Objects.equals(this.query.getValue(), query)) {
            return;
        }
        this.query.onNext(query);
    }

    public Observable<List<RecentSearch>> getRecentSearches(int limit) {
        return recentSearchRepository.getRecentSearches(limit);
    }

    public void clearRecentSearches() {
        disposables.add(
                recentSearchRepository
                        .clearRecentSearches()
                        .subscribeOn(ioScheduler)
                        .subscribe()
        );
    }

    public void onSearchTriggered(String query) {
        disposables.add(
                recentSearchRepository
                        .insertOrReplaceRecentSearch(query)
                        .subscribeOn(ioScheduler)
                        .subscribe()
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

}