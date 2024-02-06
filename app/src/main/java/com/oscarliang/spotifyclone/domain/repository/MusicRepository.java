package com.oscarliang.spotifyclone.domain.repository;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.List;

public interface MusicRepository {

    LiveData<Resource<List<Music>>> getMusicsByAlbumId(String albumId);

    LiveData<Resource<List<Music>>> getMusicsByCategory(String category, int maxResult);

    LiveData<Resource<List<Music>>> getMusicsByIds(List<String> ids);

    LiveData<Resource<List<Music>>> search(String query, int maxResult);

}
