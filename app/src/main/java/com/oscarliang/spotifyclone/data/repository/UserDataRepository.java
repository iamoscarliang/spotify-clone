package com.oscarliang.spotifyclone.data.repository;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.oscarliang.spotifyclone.data.datasource.FirebaseAuthResource;
import com.oscarliang.spotifyclone.domain.repository.UserRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserDataRepository implements UserRepository {

    private final FirebaseAuth mAuth;

    @Inject
    public UserDataRepository(FirebaseAuth auth) {
        mAuth = auth;
    }

    @Override
    public LiveData<Event<Resource<AuthResult>>> createUser(String email, String password) {
        return new FirebaseAuthResource<AuthResult>() {
            @Override
            protected Task<AuthResult> createCall() {
                return mAuth.createUserWithEmailAndPassword(email, password);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Event<Resource<AuthResult>>> loginUser(String email, String password) {
        return new FirebaseAuthResource<AuthResult>() {
            @Override
            protected Task<AuthResult> createCall() {
                return mAuth.signInWithEmailAndPassword(email, password);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Event<Resource<Void>>> resetPassword(String email) {
        return new FirebaseAuthResource<Void>() {
            @Override
            protected Task<Void> createCall() {
                return mAuth.sendPasswordResetEmail(email);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Event<Resource<Void>>> updateProfile(UserProfileChangeRequest profileUpdates) {
        return new FirebaseAuthResource<Void>() {
            @Override
            protected Task<Void> createCall() {
                return mAuth
                        .getCurrentUser()
                        .updateProfile(profileUpdates);
            }
        }.getLiveData();
    }

}
