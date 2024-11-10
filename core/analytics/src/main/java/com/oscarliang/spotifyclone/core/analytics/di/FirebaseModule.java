package com.oscarliang.spotifyclone.core.analytics.di;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    @SuppressLint("MissingPermission")
    @Singleton
    @Provides
    public FirebaseAnalytics provideFirebaseAnalytics(
            Application application
    ) {
        return FirebaseAnalytics.getInstance(application);
    }

}