package com.oscarliang.spotifyclone.core.data.repository;

import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.data.mapper.MusicMapper;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.network.api.MusicService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class DefaultMusicRepository implements MusicRepository {

    private final MusicService service;

    @Inject
    public DefaultMusicRepository(MusicService service) {
        this.service = service;
    }

    @Override
    public Single<Music> getMusicsById(String id) {
        return service
                .getMusicsById(id, Source.DEFAULT)
                .map(entity -> MusicMapper.map(entity));
    }

    @Override
    public Single<Music> getCachedMusicsById(String id) {
        return service
                .getMusicsById(id, Source.CACHE)
                .map(entity -> MusicMapper.map(entity));
    }

    @Override
    public Single<List<Music>> getMusicsByIds(List<String> ids) {
        return service
                .getMusicsByIds(ids, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> MusicMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Music>> getCachedMusicsByIds(List<String> ids) {
        return service
                .getMusicsByIds(ids, Source.CACHE)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> MusicMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Music>> getMusicsByAlbumId(String albumId) {
        return service
                .getMusicsByAlbumId(albumId, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> MusicMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Music>> getMusicsByCategoryId(String categoryId, int count) {
        return service
                .getMusicsByCategoryId(categoryId, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> MusicMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Music>> getMusicsByCategoryIdNextPage(String categoryId, int count) {
        return service
                .getMusicsByCategoryIdNextPage(categoryId, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> MusicMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Music>> search(String query, int count) {
        return service
                .search(query, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> MusicMapper.map(entity))
                                .toList()
                );
    }

    @Override
    public Single<List<Music>> searchNextPage(String query, int count) {
        return service
                .searchNextPage(query, count, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> MusicMapper.map(entity))
                                .toList()
                );
    }

}