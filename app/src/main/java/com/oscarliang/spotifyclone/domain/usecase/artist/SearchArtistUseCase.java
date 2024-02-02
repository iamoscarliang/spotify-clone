package com.oscarliang.spotifyclone.domain.usecase.artist;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Artist;

import java.util.List;

import javax.inject.Inject;

public class SearchArtistUseCase {

    private final ArtistRepository mRepository;

    @Inject
    public SearchArtistUseCase(ArtistRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Artist>>> execute(String query, int maxResult) {
        return mRepository.search(query, maxResult);
    }

}
