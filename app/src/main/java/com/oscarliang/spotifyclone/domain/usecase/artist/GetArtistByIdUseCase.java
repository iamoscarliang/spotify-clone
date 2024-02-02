package com.oscarliang.spotifyclone.domain.usecase.artist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Artist;

import javax.inject.Inject;

public class GetArtistByIdUseCase {

    private final ArtistRepository mRepository;

    @Inject
    public GetArtistByIdUseCase(ArtistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<Artist>> execute(String id) {
        return mRepository.getArtistById(id);
    }

}
