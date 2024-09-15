package com.oscarliang.spotifyclone.core.data.repository;

import com.oscarliang.spotifyclone.core.auth.api.AuthService;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class DefaultAuthRepository implements AuthRepository {

    private final AuthService service;

    @Inject
    public DefaultAuthRepository(AuthService service) {
        this.service = service;
    }

    @Override
    public Completable signup(String name, String email, String password) {
        return service
                .signup(email, password)
                .andThen(service.setUserName(name));
    }

    @Override
    public Completable login(String email, String password) {
        return service.login(email, password);
    }

    @Override
    public Completable resetPassword(String email) {
        return service.resetPassword(email);
    }

}