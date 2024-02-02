package com.oscarliang.spotifyclone.domain.usecase.playlist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;

import javax.inject.Inject;

public class AddMusicToPlaylistUseCase {

    private final PlaylistRepository mRepository;

    @Inject
    public AddMusicToPlaylistUseCase(PlaylistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<Playlist>>> execute(String userId, Playlist playlist, Music music) {
        // Add new music and update playlist image if need
        if (playlist.getMusicIds().isEmpty()) {
            playlist.setImageUrl(music.getImageUrl());
        }
        playlist.getMusicIds().add(music.getId());

        return mRepository.updatePlaylist(userId, playlist);
    }

}
