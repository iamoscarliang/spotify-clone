package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.ArtistEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class FirebaseArtistService implements ArtistService {

    private final FirebaseFirestore db;

    private final Map<String, DocumentSnapshot> page = new HashMap<>();

    @Inject
    public FirebaseArtistService(FirebaseFirestore db) {
        this.db = db;
    }

    @Override
    public Single<List<ArtistEntity>> getAllArtists(int count, Source source) {
        return Single.create(emitter ->
                db.collection("artists")
                        .orderBy("name")
                        .limit(count)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<ArtistEntity> data = task.getResult().toObjects(ArtistEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<ArtistEntity> getArtistById(String id, Source source) {
        return Single.create(emitter ->
                db.collection("artists")
                        .document(id)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && task.getResult().exists()) {
                                ArtistEntity data = task.getResult().toObject(ArtistEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<ArtistEntity>> search(String query, int count, Source source) {
        return Single.create(emitter ->
                db.collection("artists")
                        .whereGreaterThanOrEqualTo("name", query)
                        .whereLessThanOrEqualTo("name", query + "\uf8ff")
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
                                List<ArtistEntity> data = task.getResult().toObjects(ArtistEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<List<ArtistEntity>> searchNextPage(String query, int count, Source source) {
        return Single.create(emitter ->
                db.collection("artists")
                        .whereGreaterThanOrEqualTo("name", query)
                        .whereLessThanOrEqualTo("name", query + "\uf8ff")
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
                                List<ArtistEntity> data = task.getResult().toObjects(ArtistEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

}