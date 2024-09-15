package com.oscarliang.spotifyclone.core.data.repository;

import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.core.model.Category;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface CategoryRepository {

    Single<Category> getCategoryById(String id);

    Single<List<Category>> getAllCategories(Query.Direction direction);

}