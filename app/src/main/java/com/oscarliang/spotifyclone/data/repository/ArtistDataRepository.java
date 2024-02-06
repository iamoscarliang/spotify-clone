package com.oscarliang.spotifyclone.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetDocumentResource;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetQueryResource;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Artist;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ArtistDataRepository implements ArtistRepository {

    private final FirebaseFirestore mDb;

    @Inject
    public ArtistDataRepository(FirebaseFirestore db) {
        mDb = db;
    }

    @Override
    public LiveData<Resource<List<Artist>>> getAllArtists(int maxResult) {
        return new FirestoreGetQueryResource<List<Artist>>(Artist.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("artists")
                        .orderBy("name")
                        .limit(maxResult);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<Artist>> getArtistById(String id) {
        return new FirestoreGetDocumentResource<Artist>(Artist.class) {
            @NonNull
            @Override
            protected DocumentReference createCall() {
                return mDb
                        .collection("artists")
                        .document(id);
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Resource<List<Artist>>> search(String query, int maxResult) {
        return new FirestoreGetQueryResource<List<Artist>>(Artist.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("artists")
                        .whereGreaterThanOrEqualTo("name", query)
                        .whereLessThanOrEqualTo("name", query + "\uf8ff")
                        .orderBy("name")
                        .limit(maxResult);
            }
        }.getLiveData();
    }

}
