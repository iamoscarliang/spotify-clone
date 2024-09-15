package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.ArtistEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface ArtistService {

    Single<ArtistEntity> getArtistById(String id, Source source);

    Single<List<ArtistEntity>> getAllArtists(int count, Source source);

    Single<List<ArtistEntity>> search(String query, int count, Source source);

    Single<List<ArtistEntity>> searchNextPage(String query, int count, Source source);

}