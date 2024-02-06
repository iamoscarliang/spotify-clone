package com.oscarliang.spotifyclone.data.repository;

import static com.google.firebase.firestore.Query.Direction;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetDocumentResource;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetQueryResource;
import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.repository.AlbumRepository;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlbumDataRepository implements AlbumRepository {

    private final FirebaseFirestore mDb;

    @Inject
    public AlbumDataRepository(FirebaseFirestore db) {
        mDb = db;
    }

    @Override
    public LiveData<Resource<List<Album>>> getAllAlbums(int maxResult) {
        return new FirestoreGetQueryResource<List<Album>>(Album.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("albums")
                        .orderBy("title")
                        .limit(maxResult);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<List<Album>>> getLatestAlbums(int maxResult) {
        return new FirestoreGetQueryResource<List<Album>>(Album.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("albums")
                        .orderBy("year", Direction.DESCENDING)
                        .limit(maxResult);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<Album>> getAlbumById(String id) {
        return new FirestoreGetDocumentResource<Album>(Album.class) {
            @NonNull
            @Override
            protected DocumentReference createCall() {
                return mDb
                        .collection("albums")
                        .document(id);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<List<Album>>> getAlbumsByArtistId(String artistId) {
        return new FirestoreGetQueryResource<List<Album>>(Album.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("albums")
                        .whereEqualTo("artistId", artistId);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<List<Album>>> search(String query, int maxResult) {
        return new FirestoreGetQueryResource<List<Album>>(Album.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("albums")
                        .whereGreaterThanOrEqualTo("title", query)
                        .whereLessThanOrEqualTo("title", query + "\uf8ff")
                        .orderBy("title")
                        .limit(maxResult);
            }
        }.getLiveData();
    }

}
