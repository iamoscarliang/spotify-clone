package com.oscarliang.spotifyclone.domain.usecase.playlist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Playlist;

import java.util.Collections;

import javax.inject.Inject;

public class AddNewPlaylistUseCase {

    private final PlaylistRepository mRepository;

    @Inject
    public AddNewPlaylistUseCase(PlaylistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<Playlist>>> execute(String userId, String playlistName) {
        // Create a new empty playlist
        Playlist playlist = new Playlist(null, playlistName, "", Collections.emptyList());

        return mRepository.addPlaylist(userId, playlist);
    }

}
