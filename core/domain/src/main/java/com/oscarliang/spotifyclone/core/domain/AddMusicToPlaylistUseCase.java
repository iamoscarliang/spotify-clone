package com.oscarliang.spotifyclone.core.domain;

import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.model.Playlist;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class AddMusicToPlaylistUseCase {

    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;

    @Inject
    public AddMusicToPlaylistUseCase(
            MusicRepository musicRepository,
            PlaylistRepository playlistRepository
    ) {
        this.musicRepository = musicRepository;
        this.playlistRepository = playlistRepository;
    }

    public Completable execute(
            Playlist playlist,
            String musicId
    ) {
        return musicRepository
                .getCachedMusicsById(musicId)
                .flatMapCompletable(music -> {
                    // Add playlist image if need
                    if (playlist.getImageUrl() == null || playlist.getImageUrl().isEmpty()) {
                        playlist.setImageUrl(music.getImageUrl());
                    }

                    // Create a new list to prevent writing to a immutable list
                    List<String> updated = new ArrayList<>(playlist.getMusicIds());
                    updated.add(music.getId());
                    playlist.setMusicIds(updated);
                    return playlistRepository.updatePlaylist(playlist);
                });
    }

}