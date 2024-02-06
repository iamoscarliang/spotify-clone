package com.oscarliang.spotifyclone.domain.repository;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Album;

import java.util.List;

public interface AlbumRepository {

    LiveData<Resource<List<Album>>> getAllAlbums(int maxResult);

    LiveData<Resource<List<Album>>> getLatestAlbums(int maxResult);

    LiveData<Resource<List<Album>>> getAlbumsByArtistId(String artistId);

    LiveData<Resource<Album>> getAlbumById(String id);

    LiveData<Resource<List<Album>>> search(String query, int maxResult);

}
