package com.oscarliang.spotifyclone.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oscarliang.spotifyclone.domain.repository.CategoryRepository;
import com.oscarliang.spotifyclone.data.datasource.FirestoreGetCollectionResource;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Category;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CategoryDataRepository implements CategoryRepository {

    private final FirebaseFirestore mDb;

    @Inject
    public CategoryDataRepository(FirebaseFirestore db) {
        mDb = db;
    }

    @Override
    public LiveData<Resource<List<Category>>> getAllCategories() {
        return new FirestoreGetCollectionResource<List<Category>>(Category.class) {
            @NonNull
            @Override
            protected CollectionReference createCall() {
                return mDb.collection("categories");
            }
        }.getLiveData();
    }

}
