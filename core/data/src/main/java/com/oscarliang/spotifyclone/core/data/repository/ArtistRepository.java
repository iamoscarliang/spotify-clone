package com.oscarliang.spotifyclone.core.data.repository;

import com.oscarliang.spotifyclone.core.model.Artist;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface ArtistRepository {

    Single<Artist> getArtistById(String id);

    Single<List<Artist>> getAllArtists(int count);

    Single<List<Artist>> search(String query, int count);

    Single<List<Artist>> searchNextPage(String query, int count);

}