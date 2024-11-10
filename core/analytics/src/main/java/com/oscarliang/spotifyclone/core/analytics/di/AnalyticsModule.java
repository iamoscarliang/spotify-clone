package com.oscarliang.spotifyclone.core.analytics.di;

import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.FirebaseAnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.StubAnalyticsLogger;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = FirebaseModule.class)
public abstract class AnalyticsModule {

    @Singleton
    @Binds
    public abstract AnalyticsLogger bindAnalyticsLogger(
            StubAnalyticsLogger analyticsLogger
    );

}