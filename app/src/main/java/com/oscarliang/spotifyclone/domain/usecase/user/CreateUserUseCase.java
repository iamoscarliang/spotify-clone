package com.oscarliang.spotifyclone.domain.usecase.user;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.domain.repository.UserRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class CreateUserUseCase {

    private final UserRepository mRepository;

    @Inject
    public CreateUserUseCase(UserRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<AuthResult>>> execute(String email, String password) {
        return mRepository.createUser(email, password);
    }

}
