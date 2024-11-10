package com.oscarliang.spotifyclone.core.auth.di;

import com.oscarliang.spotifyclone.core.auth.AuthManager;
import com.oscarliang.spotifyclone.core.auth.FirebaseAuthManager;

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