package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenSource;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SnapshotListenOptions;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.PlaylistEntity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class FirebasePlaylistService implements PlaylistService {

    private final FirebaseFirestore db;

    @Inject
    public FirebasePlaylistService(FirebaseFirestore db) {
        this.db = db;
    }

    @Override
    public Single<PlaylistEntity> getPlaylistById(String id, Source source) {
        return Single.create(emitter ->
                db.collection("playlists")
                        .document(id)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && task.getResult().exists()) {
                                PlaylistEntity data = task.getResult().toObject(PlaylistEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        })
        );
    }

    @Override
    public Observable<List<PlaylistEntity>> getPlaylistsByUserId(String userId, ListenSource source) {
        return Observable.create(emitter -> {
                    ListenerRegistration registration = db.collection("playlists")
                            .whereEqualTo("userId", userId)
                            .orderBy("name")
                            .addSnapshotListener(
                                    new SnapshotListenOptions.Builder()
                                            .setSource(source)
                                            .build(),
                                    (value, e) -> {
                                        if (emitter.isDisposed()) {
                                            return;
                                        }
                                        if (e != null) {
                                            emitter.onError(e);
                                            return;
                                        }
                                        if (value == null) {
                                            emitter.onError(new Exception("Connection error"));
                                            return;
                                        }
                                        List<PlaylistEntity> data = value.toObjects(PlaylistEntity.class);
                                        emitter.onNext(data);
                                    }
                            );
                    // Remove the listener when downstream disposed
                    emitter.setCancellable(() -> registration.remove());
                }
        );
    }

    @Override
    public Completable createPlaylist(PlaylistEntity playlist) {
        return Completable.create(emitter ->
                db.collection("playlists")
                        .add(playlist)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        })
        );
    }

    @Override
    public Completable deletePlaylist(String id) {
        return Completable.create(emitter ->
                db.collection("playlists")
                        .document(id)
                        .delete()
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        })
        );
    }

    @Override
    public Completable updatePlaylist(PlaylistEntity playlist) {
        return Completable.create(emitter ->
                db.collection("playlists")
                        .document(playlist.getId())
                        .set(playlist)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        })
        );
    }

}