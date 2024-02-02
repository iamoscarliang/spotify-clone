package com.oscarliang.spotifyclone.domain.usecase.playlist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Playlist;

import java.util.List;

import javax.inject.Inject;

public class GetPlaylistsByUserIdUseCase {

    private final PlaylistRepository mRepository;

    @Inject
    public GetPlaylistsByUserIdUseCase(PlaylistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Playlist>>> execute(String userId) {
        return mRepository.getPlaylistsByUserId(userId);
    }

}
