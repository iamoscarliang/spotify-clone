package com.oscarliang.spotifyclone.core.data.repository;

import io.reactivex.rxjava3.core.Completable;

public interface AuthRepository {

    Completable signup(String name, String email, String password);

    Completable login(String email, String password);

    Completable resetPassword(String email);

}