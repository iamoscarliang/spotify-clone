package com.oscarliang.spotifyclone.core.auth;

import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface AuthManager {

    Completable signup(String email, String password);

    Completable login(String email, String password);

    Completable resetPassword(String email);

    Completable setUserName(String name);

    Observable<FirebaseUser> getUser();

    String getUserId();

    boolean hasUser();

    void signOut();

}