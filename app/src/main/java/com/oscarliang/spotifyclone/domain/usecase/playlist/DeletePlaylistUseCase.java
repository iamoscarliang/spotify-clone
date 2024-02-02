package com.oscarliang.spotifyclone.domain.usecase.playlist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class DeletePlaylistUseCase {

    private final PlaylistRepository mRepository;

    @Inject
    public DeletePlaylistUseCase(PlaylistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<Playlist>>> execute(String userId, Playlist playlist) {
        return mRepository.deletePlaylist(userId, playlist);
    }

}
