package com.oscarliang.spotifyclone.domain.usecase.album;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.AlbumRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Album;

import java.util.List;

import javax.inject.Inject;

public class SearchAlbumUseCase {

    private final AlbumRepository mRepository;

    @Inject
    public SearchAlbumUseCase(AlbumRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Album>>> execute(String query, int maxResult) {
        return mRepository.search(query, maxResult);
    }

}
