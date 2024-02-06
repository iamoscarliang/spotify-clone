package com.oscarliang.spotifyclone.domain.repository;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Artist;

import java.util.List;

public interface ArtistRepository {

    LiveData<Resource<List<Artist>>> getAllArtists(int maxResult);

    LiveData<Resource<Artist>> getArtistById(String id);

    LiveData<Resource<List<Artist>>> search(String query, int maxResult);

}
