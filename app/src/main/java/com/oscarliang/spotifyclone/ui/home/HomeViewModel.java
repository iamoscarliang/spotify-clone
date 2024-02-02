package com.oscarliang.spotifyclone.ui.home;

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

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {

    private final LiveData<Resource<List<Album>>> mLatestAlbums;
    private final LiveData<Resource<List<Album>>> mAllAlbums;
    private final LiveData<Resource<List<Artist>>> mAllArtists;

    private final MutableLiveData<Integer> mLatestAlbumsMaxResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> mAllAlbumsMaxResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> mAllArtistsMaxResult = new MutableLiveData<>();

    @Inject
    public HomeViewModel(GetLatestAlbumsUseCase getLatestAlbumsUseCase,
                         GetAllAlbumsUseCase getAllAlbumsUseCase,
                         GetAllArtistsUseCase getAllArtistsUseCase) {
        mLatestAlbums = Transformations.switchMap(mLatestAlbumsMaxResult, maxResult -> {
            if (maxResult == null || maxResult == 0) {
                return AbsentLiveData.create();
            } else {
                return getLatestAlbumsUseCase.execute(maxResult);
            }
        });
        mAllAlbums = Transformations.switchMap(mAllAlbumsMaxResult, maxResult -> {
            if (maxResult == null || maxResult == 0) {
                return AbsentLiveData.create();
            } else {
                return getAllAlbumsUseCase.execute(maxResult);
            }
        });
        mAllArtists = Transformations.switchMap(mAllArtistsMaxResult, maxResult -> {
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

    public void setLatestAlbums(int maxResult) {
        mLatestAlbumsMaxResult.setValue(maxResult);
    }

    public void setAllAlbums(int maxResult) {
        mAllAlbumsMaxResult.setValue(maxResult);
    }

    public void setAllArtists(int maxResult) {
        mAllArtistsMaxResult.setValue(maxResult);
    }

}
