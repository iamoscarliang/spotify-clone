package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.ListenSource;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.PlaylistEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface PlaylistService {

    Single<PlaylistEntity> getPlaylistById(String id, Source source);

    Observable<List<PlaylistEntity>> getPlaylistsByUserId(String userId, ListenSource source);

    Completable createPlaylist(PlaylistEntity playlist);

    Completable deletePlaylist(String id);

    Completable updatePlaylist(PlaylistEntity playlist);

}