package com.oscarliang.spotifyclone.core.auth.api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;

@Singleton
public class FirebaseAuthService implements AuthService {

    private final FirebaseAuth auth;

    @Inject
    public FirebaseAuthService(FirebaseAuth auth) {
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

}