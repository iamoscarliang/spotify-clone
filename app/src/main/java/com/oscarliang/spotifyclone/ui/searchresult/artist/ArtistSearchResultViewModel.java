package com.oscarliang.spotifyclone.ui.searchresult.artist;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Artist;
import com.oscarliang.spotifyclone.domain.usecase.artist.SearchArtistUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.PageQuery;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class ArtistSearchResultViewModel extends ViewModel {

    private final LiveData<Resource<List<Artist>>> mArtists;

    @VisibleForTesting
    final MutableLiveData<PageQuery> mQuery = new MutableLiveData<>();

    @Inject
    public ArtistSearchResultViewModel(SearchArtistUseCase searchArtistUseCase) {
        mArtists = Transformations.switchMap(mQuery, query -> {
            if (query == null || query.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return searchArtistUseCase.execute(query.mQuery, query.mResultPerPage * query.mPage);
            }
        });
    }

    public LiveData<Resource<List<Artist>>> getArtists() {
        return mArtists;
    }

    public void setQuery(String input, int resultPerPage) {
        PageQuery query = new PageQuery(input, resultPerPage, 1);
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

}
