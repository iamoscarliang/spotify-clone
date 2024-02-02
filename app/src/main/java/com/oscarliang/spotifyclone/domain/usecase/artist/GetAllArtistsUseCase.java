package com.oscarliang.spotifyclone.domain.usecase.artist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Artist;

import java.util.List;

import javax.inject.Inject;

public class GetAllArtistsUseCase {

    private final ArtistRepository mRepository;

    @Inject
    public GetAllArtistsUseCase(ArtistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Artist>>> execute(int maxResult) {
        return mRepository.getAllArtists(maxResult);
    }

}
