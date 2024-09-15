package com.oscarliang.spotifyclone.di;

import android.app.Application;
import android.content.ComponentName;

import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.oscarliang.spotifyclone.SpotifyService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Singleton
    @Provides
    public ComponentName provideComponentName(Application application) {
        return new ComponentName(application, SpotifyService.class);
    }

    @Singleton
    @Provides
    public SessionToken provideSessionToken(
            Application application,
            ComponentName componentName
    ) {
        return new SessionToken(application, componentName);
    }

    @Singleton
    @Provides
    public ListenableFuture<MediaController> provideMediaController(
            Application application,
            SessionToken sessionToken
    ) {
        return new MediaController.Builder(application, sessionToken).buildAsync();
    }

}