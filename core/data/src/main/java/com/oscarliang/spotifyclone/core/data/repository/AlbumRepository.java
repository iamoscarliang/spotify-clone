package com.oscarliang.spotifyclone.core.data.repository;

import com.oscarliang.spotifyclone.core.model.Album;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface AlbumRepository {

    Single<Album> getAlbumById(String id);

    Single<List<Album>> getAlbumsByArtistId(String artistId);

    Single<List<Album>> getAllAlbums(int count);

    Single<List<Album>> search(String query, int count);

    Single<List<Album>> searchNextPage(String query, int count);

}