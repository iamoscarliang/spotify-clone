package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.CategoryEntity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class FirebaseCategoryService implements CategoryService {

    private final FirebaseFirestore db;

    @Inject
    public FirebaseCategoryService(FirebaseFirestore db) {
        this.db = db;
    }

    @Override
    public Single<List<CategoryEntity>> getAllCategories(Query.Direction direction, Source source) {
        return Single.create(emitter ->
                db.collection("categories")
                        .orderBy("name", direction)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<CategoryEntity> data = task.getResult().toObjects(CategoryEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

    @Override
    public Single<CategoryEntity> getCategoryById(String id, Source source) {
        return Single.create(emitter ->
                db.collection("categories")
                        .document(id)
                        .get(source)
                        .addOnCompleteListener(task -> {
                            if (emitter.isDisposed()) {
                                return;
                            }
                            if (task.isSuccessful() && task.getResult().exists()) {
                                CategoryEntity data = task.getResult().toObject(CategoryEntity.class);
                                emitter.onSuccess(data);
                            } else {
                                Exception e = task.getException();
                                emitter.onError(e != null ? e : new Exception("Connection error"));
                            }
                        }));
    }

}