package com.oscarliang.spotifyclone.core.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.oscarliang.spotifyclone.core.auth.AuthManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class FirebaseAuthManager implements AuthManager {

    private final FirebaseAuth auth;

    @Inject
    public FirebaseAuthManager(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public Completable signup(String email, String password) {
        return Completable.create(emitter ->
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Unknown error"));
                            }
                        }));
    }

    @Override
    public Completable login(String email, String password) {
        return Completable.create(emitter ->
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Unknown error"));
                            }
                        }));
    }

    @Override
    public Completable resetPassword(String email) {
        return Completable.create(emitter ->
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Unknown error"));
                            }
                        }));
    }

    @Override
    public Completable setUserName(String name) {
        return Completable.create(emitter ->
                auth.getCurrentUser()
                        .updateProfile(
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                        )
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Unknown error"));
                            }
                        }));
    }

    @Override
    public Observable<FirebaseUser> getUser() {
        return Observable.create(emitter ->
                auth.addAuthStateListener(auth -> {
                    if (auth != null && auth.getCurrentUser() != null) {
                        emitter.onNext(auth.getCurrentUser());
                    }
                }));
    }

    @Override
    public String getUserId() {
        return auth.getCurrentUser().getUid();
    }

    @Override
    public boolean hasUser() {
        return auth.getCurrentUser() != null;
    }

    @Override
    public void signOut() {
        auth.signOut();
    }

}