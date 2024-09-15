package com.oscarliang.spotifyclone.core.auth.di;

import com.oscarliang.spotifyclone.core.auth.api.AuthService;
import com.oscarliang.spotifyclone.core.auth.api.FirebaseAuthService;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = FirebaseModule.class)
public abstract class AuthModule {

    @Singleton
    @Binds
    public abstract AuthService bindAuthService(
            FirebaseAuthService authService
    );

}