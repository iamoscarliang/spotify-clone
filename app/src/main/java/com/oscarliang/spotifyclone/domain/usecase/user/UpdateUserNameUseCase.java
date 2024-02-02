package com.oscarliang.spotifyclone.domain.usecase.user;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.UserProfileChangeRequest;
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
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        return mRepository.updateProfile(profileUpdates);
    }

}
