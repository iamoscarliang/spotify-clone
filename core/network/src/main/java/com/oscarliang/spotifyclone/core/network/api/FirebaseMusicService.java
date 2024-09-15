package com.oscarliang.spotifyclone.core.network.api;

import static java.util.Collections.emptyList;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.MusicEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class FirebaseMusicService implements MusicService {

    private final FirebaseFirestore db;

    private final Map<String, DocumentSnapshot> page = new HashMap<>();

    @Inject
    public FirebaseMusicService(FirebaseFirestore db) {
        this.db = db;
    }

    @Override
    public Single<MusicEntity> getMusicsById(String id, Source source) {
        return Single.create(emitter ->
                db.collection("musics")
                        .document(id)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && task.getResult().exists()) {
                                MusicEntity data = task.getResult().toObject(MusicEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<MusicEntity>> getMusicsByIds(List<String> ids, Source source) {
        // A non-empty array is required for 'in' filters
        if (ids.isEmpty()) {
            return Single.just(emptyList());
        }
        return Single.create(emitter ->
                db.collection("musics")
                        .whereIn(FieldPath.documentId(), ids)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<MusicEntity> data = task.getResult().toObjects(MusicEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<MusicEntity>> getMusicsByAlbumId(String albumId, Source source) {
        return Single.create(emitter ->
                db.collection("musics")
                        .whereEqualTo("albumId", albumId)
                        .orderBy("title")
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<MusicEntity> data = task.getResult().toObjects(MusicEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<MusicEntity>> getMusicsByCategoryId(String categoryId, int count, Source source) {
        return Single.create(emitter ->
                db.collection("musics")
                        .whereArrayContains("categoryIds", categoryId)
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
                                    DocumentSnapshot lastVisible = result
                                            .getDocuments().get(result.size() - 1);
                                    page.put(categoryId, lastVisible);
                                }

                                List<MusicEntity> data = result.toObjects(MusicEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<MusicEntity>> getMusicsByCategoryIdNextPage(String categoryId, int count, Source source) {
        return Single.create(emitter ->
                db.collection("musics")
                        .whereArrayContains("categoryIds", categoryId)
                        .startAfter(page.get(categoryId))
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
                                    page.put(categoryId, documentSnapshot);
                                }

                                List<MusicEntity> data = result.toObjects(MusicEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<MusicEntity>> search(String query, int count, Source source) {
        return Single.create(emitter ->
                db.collection("musics")
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
                                List<MusicEntity> data = result.toObjects(MusicEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<MusicEntity>> searchNextPage(String query, int count, Source source) {
        return Single.create(emitter ->
                db.collection("musics")
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
                                List<MusicEntity> data = result.toObjects(MusicEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

}