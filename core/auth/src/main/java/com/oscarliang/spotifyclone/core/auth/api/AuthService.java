package com.oscarliang.spotifyclone.core.auth.api;

import io.reactivex.rxjava3.core.Completable;

public interface AuthService {

    Completable signup(String email, String password);

    Completable login(String email, String password);

    Completable resetPassword(String email);

    Completable setUserName(String name);

}