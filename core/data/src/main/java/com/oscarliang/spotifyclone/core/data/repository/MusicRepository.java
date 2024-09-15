package com.oscarliang.spotifyclone.core.data.repository;

import com.oscarliang.spotifyclone.core.model.Music;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface MusicRepository {

    Single<Music> getMusicsById(String id);

    Single<Music> getCachedMusicsById(String id);

    Single<List<Music>> getMusicsByIds(List<String> ids);

    Single<List<Music>> getCachedMusicsByIds(List<String> ids);

    Single<List<Music>> getMusicsByAlbumId(String albumId);

    Single<List<Music>> getMusicsByCategoryId(String categoryId, int count);

    Single<List<Music>> getMusicsByCategoryIdNextPage(String categoryId, int count);

    Single<List<Music>> search(String query, int count);

    Single<List<Music>> searchNextPage(String query, int count);

}