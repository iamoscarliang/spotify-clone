package com.oscarliang.spotifyclone.domain.repository;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;

public interface PlaylistRepository {

    LiveData<Resource<List<Playlist>>> getPlaylistsByUserId(String userId);

    LiveData<Event<Resource<Playlist>>> addPlaylist(String userId, Playlist playlist);

    LiveData<Event<Resource<Playlist>>> updatePlaylist(String userId, Playlist playlist);

    LiveData<Event<Resource<Playlist>>> deletePlaylist(String userId, Playlist playlist);

}
