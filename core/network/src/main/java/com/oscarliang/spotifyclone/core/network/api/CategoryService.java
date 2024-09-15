package com.oscarliang.spotifyclone.core.network.api;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.network.model.CategoryEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface CategoryService {

    Single<CategoryEntity> getCategoryById(String id, Source source);

    Single<List<CategoryEntity>> getAllCategories(Query.Direction direction, Source source);

}