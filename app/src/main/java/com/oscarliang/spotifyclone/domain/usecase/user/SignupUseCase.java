package com.oscarliang.spotifyclone.domain.usecase.user;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.domain.repository.UserRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class SignupUseCase {

    private final UserRepository mRepository;

    @Inject
    public SignupUseCase(UserRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<AuthResult>>> execute(String email, String password) {
        return mRepository.signup(email, password);
    }

}
