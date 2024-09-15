package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.MusicEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface MusicService {

    Single<MusicEntity> getMusicsById(String id, Source source);

    Single<List<MusicEntity>> getMusicsByIds(List<String> ids, Source source);

    Single<List<MusicEntity>> getMusicsByAlbumId(String albumId, Source source);

    Single<List<MusicEntity>> getMusicsByCategoryId(String categoryId, int count, Source source);

    Single<List<MusicEntity>> getMusicsByCategoryIdNextPage(String categoryId, int count, Source source);

    Single<List<MusicEntity>> search(String query, int count, Source source);

    Single<List<MusicEntity>> searchNextPage(String query, int count, Source source);

}