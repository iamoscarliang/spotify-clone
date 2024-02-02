package com.oscarliang.spotifyclone.ui.album;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.usecase.album.GetAlbumByIdUseCase;
import com.oscarliang.spotifyclone.domain.usecase.music.GetMusicsByAlbumIdUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class AlbumViewModel extends ViewModel {

    private final LiveData<Resource<Album>> mAlbum;
    private final LiveData<Resource<List<Music>>> mMusics;

    private final MutableLiveData<String> mAlbumId = new MutableLiveData<>();

    @Inject
    public AlbumViewModel(GetAlbumByIdUseCase getAlbumByIdUseCase,
                          GetMusicsByAlbumIdUseCase getMusicsByAlbumIdUseCase) {
        mAlbum = Transformations.switchMap(mAlbumId, id -> {
            if (id == null || id.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getAlbumByIdUseCase.execute(id);
            }
        });
        mMusics = Transformations.switchMap(mAlbumId, id -> {
            if (id == null || id.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getMusicsByAlbumIdUseCase.execute(id);
            }
        });
    }

    public LiveData<Resource<Album>> getAlbum() {
        return mAlbum;
    }

    public LiveData<Resource<List<Music>>> getMusics() {
        return mMusics;
    }

    public void setAlbumId(String albumId) {
        mAlbumId.setValue(albumId);
    }

}
