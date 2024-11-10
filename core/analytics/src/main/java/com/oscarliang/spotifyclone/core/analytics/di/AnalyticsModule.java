package com.oscarliang.spotifyclone.core.analytics.di;

import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.FirebaseAnalyticsLogger;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = FirebaseModule.class)
public abstract class AnalyticsModule {

    @Singleton
    @Binds
    public abstract AnalyticsLogger bindAnalyticsLogger(
            FirebaseAnalyticsLogger analyticsLogger
    );

}