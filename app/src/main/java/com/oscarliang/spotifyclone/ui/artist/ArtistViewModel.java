package com.oscarliang.spotifyclone.ui.artist;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.model.Artist;
import com.oscarliang.spotifyclone.domain.usecase.album.GetAlbumsByArtistIdUseCase;
import com.oscarliang.spotifyclone.domain.usecase.artist.GetArtistByIdUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class ArtistViewModel extends ViewModel {

    private final LiveData<Resource<Artist>> mArtist;
    private final LiveData<Resource<List<Album>>> mAlbums;

    @VisibleForTesting
    final MutableLiveData<String> mArtistId = new MutableLiveData<>();

    @Inject
    public ArtistViewModel(GetArtistByIdUseCase getArtistByIdUseCase,
                           GetAlbumsByArtistIdUseCase getAlbumsByArtistIdUseCase) {
        mArtist = Transformations.switchMap(mArtistId, id -> {
            if (id == null || id.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getArtistByIdUseCase.execute(id);
            }
        });
        mAlbums = Transformations.switchMap(mArtistId, id -> {
            if (id == null || id.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getAlbumsByArtistIdUseCase.execute(id);
            }
        });
    }

    public LiveData<Resource<Artist>> getArtist() {
        return mArtist;
    }

    public LiveData<Resource<List<Album>>> getAlbums() {
        return mAlbums;
    }

    public void setArtistId(String artistId) {
        if (Objects.equals(mArtistId.getValue(), artistId)) {
            return;
        }
        mArtistId.setValue(artistId);
    }

    public void retry() {
        String current = mArtistId.getValue();
        if (current != null && !current.isEmpty()) {
            mArtistId.setValue(current);
        }
    }

}
