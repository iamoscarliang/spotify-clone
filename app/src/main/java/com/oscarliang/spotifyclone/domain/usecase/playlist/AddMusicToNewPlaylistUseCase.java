package com.oscarliang.spotifyclone.domain.usecase.playlist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;

import java.util.Collections;

import javax.inject.Inject;

public class AddMusicToNewPlaylistUseCase {

    private final PlaylistRepository mRepository;

    @Inject
    public AddMusicToNewPlaylistUseCase(PlaylistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<Playlist>>> execute(String userId, String playlistName, Music music) {
        // Create a new playlist with initial music
        Playlist playlist = new Playlist(null, playlistName, music.getImageUrl(),
                Collections.singletonList(music.getId()));

        return mRepository.addPlaylist(userId, playlist);
    }

}
