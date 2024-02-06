package com.oscarliang.spotifyclone.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.domain.repository.MusicRepository;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetQueryResource;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MusicDataRepository implements MusicRepository {

    private final FirebaseFirestore mDb;

    @Inject
    public MusicDataRepository(FirebaseFirestore db) {
        mDb = db;
    }

    @Override
    public LiveData<Resource<List<Music>>> getMusicsByAlbumId(String albumId) {
        return new FirestoreGetQueryResource<List<Music>>(Music.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("musics")
                        .whereEqualTo("albumId", albumId);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<List<Music>>> getMusicsByCategory(String category, int maxResult) {
        return new FirestoreGetQueryResource<List<Music>>(Music.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("musics")
                        .whereArrayContains("categories", category)
                        .limit(maxResult);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<List<Music>>> getMusicsByIds(List<String> ids) {
        return new FirestoreGetQueryResource<List<Music>>(Music.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("musics")
                        .whereIn(FieldPath.documentId(), ids);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<List<Music>>> search(String query, int maxResult) {
        return new FirestoreGetQueryResource<List<Music>>(Music.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("musics")
                        .whereGreaterThanOrEqualTo("title", query)
                        .whereLessThanOrEqualTo("title", query + "\uf8ff")
                        .orderBy("title")
                        .limit(maxResult);
            }
        }.getLiveData();
    }

}
