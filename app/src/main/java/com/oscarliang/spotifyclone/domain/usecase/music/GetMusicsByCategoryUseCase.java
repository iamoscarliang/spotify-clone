package com.oscarliang.spotifyclone.domain.usecase.music;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.MusicRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.List;

import javax.inject.Inject;

public class GetMusicsByCategoryUseCase {

    private final MusicRepository mRepository;

    @Inject
    public GetMusicsByCategoryUseCase(MusicRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Music>>> execute(String category, int maxResult) {
        return mRepository.getMusicsByCategory(category, maxResult);
    }

}
