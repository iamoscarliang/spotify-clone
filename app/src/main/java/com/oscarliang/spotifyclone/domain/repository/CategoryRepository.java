package com.oscarliang.spotifyclone.domain.repository;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Category;

import java.util.List;

public interface CategoryRepository {

    LiveData<Resource<List<Category>>> getAllCategories();

}
