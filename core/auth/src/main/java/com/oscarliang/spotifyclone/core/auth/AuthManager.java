package com.oscarliang.spotifyclone.core.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class AuthManager {

    private final FirebaseAuth auth;

    @Inject
    public AuthManager(FirebaseAuth auth) {
        this.auth = auth;
    }

    public boolean hasUser() {
        return auth.getCurrentUser() != null;
    }

    public Observable<FirebaseUser> getUser() {
        return Observable.create(emitter ->
                auth.addAuthStateListener(auth -> {
                    if (auth != null && auth.getCurrentUser() != null) {
                        emitter.onNext(auth.getCurrentUser());
                    }
                }));
    }

    public String getUserId() {
        if (!hasUser()) {
            throw new IllegalStateException("User not sign in");
        }
        return auth.getCurrentUser().getUid();
    }

    public void signOut() {
        if (!hasUser()) {
            throw new IllegalStateException("User not sign in");
        }
        auth.signOut();
    }

}