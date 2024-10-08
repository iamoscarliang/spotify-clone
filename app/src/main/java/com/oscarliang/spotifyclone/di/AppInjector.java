package com.oscarliang.spotifyclone.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.oscarliang.spotifyclone.SpotifyApp;

import dagger.android.AndroidInjection;
import dagger.android.HasAndroidInjector;
import dagger.android.support.AndroidSupportInjection;

public class AppInjector {

    private AppInjector() {
    }

    public static void init(SpotifyApp spotifyApp) {
        DaggerAppComponent.builder()
                .application(spotifyApp)
                .build()
                .inject(spotifyApp);

        spotifyApp.registerActivityLifecycleCallbacks(
                new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(
                            @NonNull Activity activity,
                            Bundle savedInstanceState
                    ) {
                        handleActivity(activity);
                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivitySaveInstanceState(
                            @NonNull Activity activity,
                            @NonNull Bundle outState
                    ) {
                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {
                    }
                }
        );
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof HasAndroidInjector) {
            AndroidInjection.inject(activity);
        }
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(
                            new FragmentManager.FragmentLifecycleCallbacks() {
                                @Override
                                public void onFragmentCreated(
                                        @NonNull FragmentManager fragmentManager,
                                        @NonNull Fragment fragment,
                                        Bundle savedInstanceState
                                ) {
                                    try {
                                        AndroidSupportInjection.inject(fragment);
                                    } catch (Exception e) {
                                        // Ignore the exception if cannot find a parent implement
                                        // HasAndroidInjector, since it do not inject anything
                                    }
                                }
                            },
                            true
                    );
        }
    }

}