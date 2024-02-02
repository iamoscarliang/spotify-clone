package com.oscarliang.spotifyclone.domain.usecase.album;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.repository.AlbumRepository;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class GetAlbumByIdUseCase {

    private final AlbumRepository mRepository;

    @Inject
    public GetAlbumByIdUseCase(AlbumRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<Album>> execute(String id) {
        return mRepository.getAlbumById(id);
    }

}
