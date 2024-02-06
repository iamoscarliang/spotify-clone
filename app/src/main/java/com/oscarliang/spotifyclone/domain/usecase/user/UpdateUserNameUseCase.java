package com.oscarliang.spotifyclone.domain.usecase.user;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.UserRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class UpdateUserNameUseCase {

    private final UserRepository mRepository;

    @Inject
    public UpdateUserNameUseCase(UserRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<Void>>> execute(String name) {
        return mRepository.setUserName(name);
    }

}
