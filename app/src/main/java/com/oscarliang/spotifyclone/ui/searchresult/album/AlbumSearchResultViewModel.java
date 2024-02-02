package com.oscarliang.spotifyclone.ui.searchresult.album;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.usecase.album.SearchAlbumUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.PageQuery;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class AlbumSearchResultViewModel extends ViewModel {

    private final LiveData<Resource<List<Album>>> mAlbums;

    private final MutableLiveData<PageQuery> mQuery = new MutableLiveData<>();

    @Inject
    public AlbumSearchResultViewModel(SearchAlbumUseCase searchAlbumUseCase) {
        mAlbums = Transformations.switchMap(mQuery, query -> {
            if (query == null || query.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return searchAlbumUseCase.execute(query.mQuery, query.mResultPerPage * query.mPage);
            }
        });
    }

    public LiveData<Resource<List<Album>>> getAlbums() {
        return mAlbums;
    }

    public void setQuery(String input, int resultPerPage) {
        PageQuery query = new PageQuery(input, resultPerPage, 1);
        if (Objects.equals(query, mQuery.getValue())) {
            return;
        }
        mQuery.setValue(query);
    }

    public void loadNextPage() {
        mQuery.setValue(PageQuery.next(mQuery.getValue()));
    }

}
