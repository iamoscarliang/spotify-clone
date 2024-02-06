package com.oscarliang.spotifyclone.domain.usecase.playlist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class UpdatePlaylistUseCase {

    private final PlaylistRepository mRepository;

    @Inject
    public UpdatePlaylistUseCase(PlaylistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Event<Resource<Playlist>>> execute(String userId, Playlist playlist,
                                                       String playlistName, List<Music> musics) {
        List<String> musicIds = new ArrayList<>();
        for (Music music : musics) {
            musicIds.add(music.getId());
        }

        // Check is playlist content change
        if (Objects.equals(playlist.getMusicIds(), musicIds)
                && Objects.equals(playlist.getName(), playlistName)) {
            return AbsentLiveData.create();
        }

        // Update the new playlist
        playlist.setName(playlistName);
        playlist.setMusicIds(musicIds);
        if (musics.isEmpty()) {
            playlist.setImageUrl("");
        } else {
            playlist.setImageUrl(musics.get(0).getImageUrl());
        }

        return mRepository.updatePlaylist(userId, playlist);
    }

}
