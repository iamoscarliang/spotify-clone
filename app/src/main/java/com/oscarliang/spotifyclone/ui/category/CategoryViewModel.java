package com.oscarliang.spotifyclone.ui.category;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.usecase.music.GetMusicsByCategoryUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.PageQuery;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {

    private final LiveData<Resource<List<Music>>> mMusics;

    @VisibleForTesting
    final MutableLiveData<PageQuery> mQuery = new MutableLiveData<>();

    @Inject
    public CategoryViewModel(GetMusicsByCategoryUseCase getMusicsByCategoryUseCase) {
        mMusics = Transformations.switchMap(mQuery, query -> {
            if (query == null || query.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getMusicsByCategoryUseCase.execute(query.mQuery, query.mResultPerPage * query.mPage);
            }
        });
    }

    public LiveData<Resource<List<Music>>> getMusics() {
        return mMusics;
    }

    public void setQuery(String category, int resultPerPage) {
        PageQuery query = new PageQuery(category, resultPerPage, 1);
        if (Objects.equals(mQuery.getValue(), query)) {
            return;
        }
        mQuery.setValue(query);
    }

    public void loadNextPage() {
        PageQuery current = mQuery.getValue();
        if (current != null && !current.isEmpty()) {
            mQuery.setValue(new PageQuery(current.mQuery, current.mResultPerPage, current.mPage + 1));
        }
    }

    public void retry() {
        PageQuery current = mQuery.getValue();
        if (current != null && !current.isEmpty()) {
            mQuery.setValue(current);
        }
    }

}
