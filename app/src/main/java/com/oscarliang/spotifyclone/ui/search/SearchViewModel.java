package com.oscarliang.spotifyclone.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Category;
import com.oscarliang.spotifyclone.domain.usecase.category.GetAllCategoriesUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class SearchViewModel extends ViewModel {

    private final LiveData<Resource<List<Category>>> mCategories;

    private final MutableLiveData<Boolean> mIsLoad = new MutableLiveData<>();

    @Inject
    public SearchViewModel(GetAllCategoriesUseCase getAllCategoriesUseCase) {
        mCategories = Transformations.switchMap(mIsLoad, isLoad -> {
            if (isLoad == null || !isLoad) {
                return AbsentLiveData.create();
            } else {
                return getAllCategoriesUseCase.execute();
            }
        });
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return mCategories;
    }

    public void setLoad(boolean load) {
        mIsLoad.setValue(load);
    }

}
