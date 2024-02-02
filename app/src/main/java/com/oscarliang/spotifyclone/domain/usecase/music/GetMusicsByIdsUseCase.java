package com.oscarliang.spotifyclone.domain.usecase.music;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.MusicRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.List;

import javax.inject.Inject;

public class GetMusicsByIdsUseCase {

    private final MusicRepository mRepository;

    @Inject
    public GetMusicsByIdsUseCase(MusicRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Music>>> execute(List<String> ids) {
        return mRepository.getMusicsByIds(ids);
    }

}
