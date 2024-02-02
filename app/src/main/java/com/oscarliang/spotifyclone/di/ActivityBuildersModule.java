package com.oscarliang.spotifyclone.di;

import com.oscarliang.spotifyclone.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    //--------------------------------------------------------
    // Methods
    //--------------------------------------------------------
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
    //========================================================

}
