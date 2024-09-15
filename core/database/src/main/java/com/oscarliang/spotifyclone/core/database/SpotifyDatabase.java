package com.oscarliang.spotifyclone.core.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.oscarliang.spotifyclone.core.database.dao.RecentSearchDao;
import com.oscarliang.spotifyclone.core.database.model.RecentSearchEntity;

@Database(entities = {RecentSearchEntity.class}, version = 1)
public abstract class SpotifyDatabase extends RoomDatabase {

    public abstract RecentSearchDao getRecentSearchDao();

}