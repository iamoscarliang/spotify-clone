package com.oscarliang.spotifyclone.domain.usecase.album;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.repository.AlbumRepository;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class GetLatestAlbumsUseCase {

    private final AlbumRepository mRepository;

    @Inject
    public GetLatestAlbumsUseCase(AlbumRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Album>>> execute(int maxResult) {
        return mRepository.getLatestAlbums(maxResult);
    }

}
