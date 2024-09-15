package com.oscarliang.spotifyclone.core.data.repository;

import com.oscarliang.spotifyclone.core.model.Playlist;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface PlaylistRepository {

    Single<Playlist> getPlaylistById(String id);

    Single<Playlist> getCachedPlaylistById(String id);

    Observable<List<Playlist>> getPlaylistsByUserId(String userId);

    Completable createPlaylist(String playlistName, String userId);

    Completable deletePlaylist(String id);

    Completable updatePlaylist(Playlist playlist);

}