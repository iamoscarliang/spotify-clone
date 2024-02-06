package com.oscarliang.spotifyclone.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.data.datasource.FirestoreAddDocumentResource;
import com.oscarliang.spotifyclone.data.datasource.FirestoreDeleteDocumentResource;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetCollectionResource;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetQueryResource;
import com.oscarliang.spotifyclone.data.datasource.FirestoreSetDocumentResource;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlaylistDataRepository implements PlaylistRepository {

    private final FirebaseFirestore mDb;

    @Inject
    public PlaylistDataRepository(FirebaseFirestore db) {
        mDb = db;
    }

    @Override
    public LiveData<Resource<List<Playlist>>> getPlaylistsByUserId(String userId) {
        return new FirestoreGetQueryResource<List<Playlist>>(Playlist.class) {
            @NonNull
            @Override
            protected Query createCall() {
                return mDb
                        .collection("users")
                        .document(userId)
                        .collection("playlists")
                        .orderBy("name");
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Event<Resource<Playlist>>> addPlaylist(String userId, Playlist playlist) {
        return new FirestoreAddDocumentResource<Playlist>(playlist) {
            @NonNull
            @Override
            protected CollectionReference createCall() {
                return mDb
                        .collection("users")
                        .document(userId)
                        .collection("playlists");
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Event<Resource<Playlist>>> updatePlaylist(String userId, Playlist playlist) {
        return new FirestoreSetDocumentResource<Playlist>(playlist) {
            @NonNull
            @Override
            protected DocumentReference createCall() {
                return mDb
                        .collection("users")
                        .document(userId)
                        .collection("playlists")
                        .document(playlist.getId());
            }
        }.getLiveData();
    }

    @Override
    public LiveData<Event<Resource<Playlist>>> deletePlaylist(String userId, Playlist playlist) {
        return new FirestoreDeleteDocumentResource<Playlist>(playlist) {
            @NonNull
            @Override
            protected DocumentReference createCall() {
                return mDb
                        .collection("users")
                        .document(userId)
                        .collection("playlists")
                        .document(playlist.getId());
            }
        }.getLiveData();
    }

}
