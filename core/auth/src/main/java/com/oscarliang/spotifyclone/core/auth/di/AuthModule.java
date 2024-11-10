package com.oscarliang.spotifyclone.core.auth.di;

import com.oscarliang.spotifyclone.core.auth.api.AuthManager;
import com.oscarliang.spotifyclone.core.auth.api.FirebaseAuthManager;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = FirebaseModule.class)
public abstract class AuthModule {

    @Singleton
    @Binds
    public abstract AuthManager bindAuthManager(
            FirebaseAuthManager authManager
    );

}