package com.oscarliang.spotifyclone.core.data.repository;

import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.data.mapper.AlbumMapper;
import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.network.api.AlbumService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class DefaultAlbumRepository implements AlbumRepository {

    private final AlbumService service;

    @Inject
    public DefaultAlbumRepository(AlbumService service) {
        this.service = service;
    }

    @Override
    public Single<Album> getAlbumById(String id) {
        return service
                .getAlbumById(id, Source.DEFAULT)
                .map(entity -> AlbumMapper.map(entity));
    }

    @Override
    public Single<List<Album>> getAlbumsByArtistId(String artistId) {
        return service
                .getAlbumsByArtistId(artistId, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> AlbumMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Album>> getAllAlbums(int count) {
        return service
                .getAllAlbums(count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> AlbumMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Album>> search(String query, int count) {
        return service
                .search(query, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> AlbumMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Album>> searchNextPage(String query, int count) {
        return service
                .searchNextPage(query, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> AlbumMapper.map(entity))
                                .toList()
                );
    }

}