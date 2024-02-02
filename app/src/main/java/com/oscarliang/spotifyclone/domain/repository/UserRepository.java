package com.oscarliang.spotifyclone.domain.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

public interface UserRepository {

    LiveData<Event<Resource<AuthResult>>> createUser(String email, String password);

    LiveData<Event<Resource<AuthResult>>> loginUser(String email, String password);

    LiveData<Event<Resource<Void>>> resetPassword(String email);

    LiveData<Event<Resource<Void>>> updateProfile(UserProfileChangeRequest profileUpdates);

}
