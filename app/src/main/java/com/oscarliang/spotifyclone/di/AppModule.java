package com.oscarliang.spotifyclone.di;

import androidx.lifecycle.ViewModelProvider;

import com.oscarliang.spotifyclone.core.analytics.di.AnalyticsModule;
import com.oscarliang.spotifyclone.core.auth.di.AuthModule;
import com.oscarliang.spotifyclone.core.common.scheduler.di.SchedulerModule;
import com.oscarliang.spotifyclone.core.data.di.DataModule;
import com.oscarliang.spotifyclone.core.database.di.DatabaseModule;
import com.oscarliang.spotifyclone.core.network.di.NetworkModule;
import com.oscarliang.spotifyclone.core.player.di.PlayerModule;
import com.oscarliang.spotifyclone.util.ViewModelFactory;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = {
        AuthModule.class,
        PlayerModule.class,
        DataModule.class,
        DatabaseModule.class,
        NetworkModule.class,
        AnalyticsModule.class,
        ViewModelModule.class,
        SchedulerModule.class
})
public abstract class AppModule {

    @Singleton
    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(
            ViewModelFactory viewModelFactory
    );

}