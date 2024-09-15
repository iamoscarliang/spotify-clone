package com.oscarliang.spotifyclone.core.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import com.oscarliang.spotifyclone.core.database.model.RecentSearchEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface RecentSearchDao {

    @Query("SELECT * FROM recent_searches ORDER BY queriedTime DESC LIMIT :limit")
    Observable<List<RecentSearchEntity>> getRecentSearches(int limit);

    @Upsert
    Completable insertOrReplaceRecentSearch(RecentSearchEntity recentSearchEntity);

    @Query("DELETE FROM recent_searches")
    Completable clearRecentSearches();

}