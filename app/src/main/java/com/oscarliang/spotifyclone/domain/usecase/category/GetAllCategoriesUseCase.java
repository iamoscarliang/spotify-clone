package com.oscarliang.spotifyclone.domain.usecase.category;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.CategoryRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Category;

import java.util.List;

import javax.inject.Inject;

public class GetAllCategoriesUseCase {

    private final CategoryRepository mRepository;

    @Inject
    public GetAllCategoriesUseCase(CategoryRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Category>>> execute() {
        return mRepository.getAllCategories();
    }

}
