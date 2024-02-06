package com.oscarliang.spotifyclone.ui.home;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.model.Artist;
import com.oscarliang.spotifyclone.domain.usecase.album.GetAllAlbumsUseCase;
import com.oscarliang.spotifyclone.domain.usecase.album.GetLatestAlbumsUseCase;
import com.oscarliang.spotifyclone.domain.usecase.artist.GetAllArtistsUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {

    private final LiveData<Resource<List<Album>>> mLatestAlbums;
    private final LiveData<Resource<List<Album>>> mAllAlbums;
    private final LiveData<Resource<List<Artist>>> mAllArtists;

    @VisibleForTesting
    final MutableLiveData<Integer> mLatestMaxResult = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<Integer> mAllMaxResult = new MutableLiveData<>();

    @Inject
    public HomeViewModel(GetLatestAlbumsUseCase getLatestAlbumsUseCase,
                         GetAllAlbumsUseCase getAllAlbumsUseCase,
                         GetAllArtistsUseCase getAllArtistsUseCase) {
        mLatestAlbums = Transformations.switchMap(mLatestMaxResult, maxResult -> {
            if (maxResult == null || maxResult == 0) {
                return AbsentLiveData.create();
            } else {
                return getLatestAlbumsUseCase.execute(maxResult);
            }
        });
        mAllAlbums = Transformations.switchMap(mAllMaxResult, maxResult -> {
            if (maxResult == null || maxResult == 0) {
                return AbsentLiveData.create();
            } else {
                return getAllAlbumsUseCase.execute(maxResult);
            }
        });
        mAllArtists = Transformations.switchMap(mAllMaxResult, maxResult -> {
            if (maxResult == null || maxResult == 0) {
                return AbsentLiveData.create();
            } else {
                return getAllArtistsUseCase.execute(maxResult);
            }
        });
    }

    public LiveData<Resource<List<Album>>> getLatestAlbums() {
        return mLatestAlbums;
    }

    public LiveData<Resource<List<Album>>> getAllAlbums() {
        return mAllAlbums;
    }

    public LiveData<Resource<List<Artist>>> getAllArtists() {
        return mAllArtists;
    }

    public void setLatest(int maxResult) {
        if (Objects.equals(mLatestMaxResult.getValue(), maxResult)) {
            return;
        }
        mLatestMaxResult.setValue(maxResult);
    }

    public void setAll(int maxResult) {
        if (Objects.equals(mAllMaxResult.getValue(), maxResult)) {
            return;
        }
        mAllMaxResult.setValue(maxResult);
    }

    public void refresh() {
        if (mLatestMaxResult.getValue() != null) {
            mLatestMaxResult.setValue(mLatestMaxResult.getValue());
        }
        if (mAllMaxResult.getValue() != null) {
            mAllMaxResult.setValue(mAllMaxResult.getValue());
        }
    }

}
