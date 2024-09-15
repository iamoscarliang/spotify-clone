package com.oscarliang.spotifyclone.core.data.repository;

import com.oscarliang.spotifyclone.core.data.mapper.RecentSearchMapper;
import com.oscarliang.spotifyclone.core.database.dao.RecentSearchDao;
import com.oscarliang.spotifyclone.core.database.model.RecentSearchEntity;
import com.oscarliang.spotifyclone.core.model.RecentSearch;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class DefaultRecentSearchRepository implements RecentSearchRepository {

    private final RecentSearchDao dao;

    @Inject
    public DefaultRecentSearchRepository(RecentSearchDao dao) {
        this.dao = dao;
    }

    @Override
    public Observable<List<RecentSearch>> getRecentSearches(int limit) {
        return dao
                .getRecentSearches(limit)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> RecentSearchMapper.map(entity))
                                .toList()
                                .toObservable()
                );
    }

    @Override
    public Completable insertOrReplaceRecentSearch(String query) {
        return dao.insertOrReplaceRecentSearch(
                new RecentSearchEntity(
                        query,
                        System.currentTimeMillis()
                )
        );
    }

    @Override
    public Completable clearRecentSearches() {
        return dao.clearRecentSearches();
    }

}