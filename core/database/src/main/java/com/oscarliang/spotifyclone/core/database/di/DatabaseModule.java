package com.oscarliang.spotifyclone.core.database.di;

import android.app.Application;

import androidx.room.Room;

import com.oscarliang.spotifyclone.core.database.SpotifyDatabase;
import com.oscarliang.spotifyclone.core.database.dao.RecentSearchDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Singleton
    @Provides
    public SpotifyDatabase provideDatabase(
            Application application
    ) {
        return Room
                .databaseBuilder(application, SpotifyDatabase.class, "spotify.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    public RecentSearchDao provideRecentSearchDao(
            SpotifyDatabase database
    ) {
        return database.getRecentSearchDao();
    }

}