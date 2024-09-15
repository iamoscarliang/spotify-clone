package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.AlbumEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface AlbumService {

    Single<AlbumEntity> getAlbumById(String id, Source source);

    Single<List<AlbumEntity>> getAlbumsByArtistId(String artistId, Source source);

    Single<List<AlbumEntity>> getAllAlbums(int count, Source source);

    Single<List<AlbumEntity>> search(String query, int count, Source source);

    Single<List<AlbumEntity>> searchNextPage(String query, int count, Source source);

}