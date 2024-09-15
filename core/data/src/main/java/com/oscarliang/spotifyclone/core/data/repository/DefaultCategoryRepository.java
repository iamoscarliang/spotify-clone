package com.oscarliang.spotifyclone.core.data.repository;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.oscarliang.spotifyclone.core.data.mapper.CategoryMapper;
import com.oscarliang.spotifyclone.core.model.Category;
import com.oscarliang.spotifyclone.core.network.api.CategoryService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class DefaultCategoryRepository implements CategoryRepository {

    private final CategoryService service;

    @Inject
    public DefaultCategoryRepository(CategoryService service) {
        this.service = service;
    }

    @Override
    public Single<Category> getCategoryById(String id) {
        return service
                .getCategoryById(id, Source.DEFAULT)
                .map(entity -> CategoryMapper.map(entity));
    }

    @Override
    public Single<List<Category>> getAllCategories(Query.Direction direction) {
        return service
                .getAllCategories(direction, Source.DEFAULT)
                .flatMap(entities ->
                        Observable.fromIterable(entities)
                                .map(entity -> CategoryMapper.map(entity))
                                .toList()
                );
    }

}