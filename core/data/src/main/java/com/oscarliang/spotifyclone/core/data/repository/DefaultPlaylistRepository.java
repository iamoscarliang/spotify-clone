package com.oscarliang.spotifyclone.core.data.repository;

import static java.util.Collections.emptyList;

import com.google.firebase.firestore.ListenSource;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.data.mapper.PlaylistMapper;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.network.api.PlaylistService;
import com.oscarliang.spotifyclone.core.network.model.PlaylistEntity;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class DefaultPlaylistRepository implements PlaylistRepository {

    private final PlaylistService service;

    @Inject
    public DefaultPlaylistRepository(PlaylistService service) {
        this.service = service;
    }

    @Override
    public Single<Playlist> getPlaylistById(String id) {
        return service
                .getPlaylistById(id, Source.DEFAULT)
                .map(entity -> PlaylistMapper.map(entity));
    }

    @Override
    public Single<Playlist> getCachedPlaylistById(String id) {
        return service
                .getPlaylistById(id, Source.CACHE)
                .map(entity -> PlaylistMapper.map(entity));
    }

    @Override
    public Observable<List<Playlist>> getPlaylistsByUserId(String userId) {
        return service
                .getPlaylistsByUserId(userId, ListenSource.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> PlaylistMapper.map(entity))
                                .toList()
                                .toObservable()
                );
    }

    @Override
    public Completable createPlaylist(String playlistName, String userId) {
        return service.createPlaylist(
                new PlaylistEntity(
                        null,
                        playlistName,
                        "",
                        userId,
                        emptyList()
                )
        );
    }

    @Override
    public Completable deletePlaylist(String id) {
        return service.deletePlaylist(id);
    }

    @Override
    public Completable updatePlaylist(Playlist playlist) {
        return service.updatePlaylist(PlaylistMapper.map(playlist));
    }

}