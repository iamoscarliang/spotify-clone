package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.AlbumEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class FirebaseAlbumService implements AlbumService {

    private final FirebaseFirestore db;

    private final Map<String, DocumentSnapshot> page = new HashMap<>();

    @Inject
    public FirebaseAlbumService(FirebaseFirestore db) {
        this.db = db;
    }

    @Override
    public Single<List<AlbumEntity>> getAllAlbums(int count, Source source) {
        return Single.create(emitter ->
                db.collection("albums")
                        .orderBy("title")
                        .limit(count)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<AlbumEntity> data = task.getResult().toObjects(AlbumEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<AlbumEntity> getAlbumById(String id, Source source) {
        return Single.create(emitter ->
                db.collection("albums")
                        .document(id)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && task.getResult().exists()) {
                                AlbumEntity data = task.getResult().toObject(AlbumEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<AlbumEntity>> getAlbumsByArtistId(String artistId, Source source) {
        return Single.create(emitter ->
                db.collection("albums")
                        .whereEqualTo("artistId", artistId)
                        .orderBy("title")
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<AlbumEntity> data = task.getResult().toObjects(AlbumEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<AlbumEntity>> search(String query, int count, Source source) {
        return Single.create(emitter ->
                db.collection("albums")
                        .whereGreaterThanOrEqualTo("title", query)
                        .whereLessThanOrEqualTo("title", query + "\uf8ff")
                        .limit(count)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful()) {
                                // If the snapshot is empty and service is offline,
                                // we always show errors since we don't know if the
                                // online data is also empty or not
                                QuerySnapshot result = task.getResult();
                                if (result.isEmpty() && result.getMetadata().isFromCache()) {
                                    emitter.onError(new Exception("Connection error"));
                                    return;
                                }

                                // Remember current page last item to query next page
                                if (!result.isEmpty()) {
                                    DocumentSnapshot documentSnapshot = result
                                            .getDocuments().get(result.size() - 1);
                                    page.put(query, documentSnapshot);
                                }
                                List<AlbumEntity> data = result.toObjects(AlbumEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<AlbumEntity>> searchNextPage(String query, int count, Source source) {
        return Single.create(emitter ->
                db.collection("albums")
                        .whereGreaterThanOrEqualTo("title", query)
                        .whereLessThanOrEqualTo("title", query + "\uf8ff")
                        .startAfter(page.get(query))
                        .limit(count)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful()) {
                                // If the snapshot is empty and service is offline,
                                // we always show errors since we don't know if the
                                // online data is also empty or not
                                QuerySnapshot result = task.getResult();
                                if (result.isEmpty() && result.getMetadata().isFromCache()) {
                                    emitter.onError(new Exception("Connection error"));
                                    return;
                                }

                                // Remember current page last item to query next page
                                if (!result.isEmpty()) {
                                    DocumentSnapshot documentSnapshot = result
                                            .getDocuments().get(result.size() - 1);
                                    page.put(query, documentSnapshot);
                                }
                                List<AlbumEntity> data = result.toObjects(AlbumEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

}