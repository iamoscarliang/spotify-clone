package com.oscarliang.spotifyclone.core.data.repository;

import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.data.mapper.ArtistMapper;
import com.oscarliang.spotifyclone.core.model.Artist;
import com.oscarliang.spotifyclone.core.network.api.ArtistService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class DefaultArtistRepository implements ArtistRepository {

    private final ArtistService service;

    @Inject
    public DefaultArtistRepository(ArtistService service) {
        this.service = service;
    }

    @Override
    public Single<Artist> getArtistById(String id) {
        return service
                .getArtistById(id, Source.DEFAULT)
                .map(entity -> ArtistMapper.map(entity));
    }

    @Override
    public Single<List<Artist>> getAllArtists(int count) {
        return service
                .getAllArtists(count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> ArtistMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Artist>> search(String query, int count) {
        return service
                .search(query, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> ArtistMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Artist>> searchNextPage(String query, int count) {
        return service
                .searchNextPage(query, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> ArtistMapper.map(entity))
                                .toList()
                );
    }

}