package com.oscarliang.spotifyclone.core.data.repository;

import com.oscarliang.spotifyclone.core.model.RecentSearch;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface RecentSearchRepository {

    Observable<List<RecentSearch>> getRecentSearches(int limit);

    Completable insertOrReplaceRecentSearch(String query);

    Completable clearRecentSearches();

}