package com.oscarliang.spotifyclone.domain.usecase.music;

import androidx.lifecycle.LiveData;

import com.oscarliang.spotifyclone.domain.repository.MusicRepository;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.List;

import javax.inject.Inject;

public class GetMusicsByAlbumIdUseCase {

    private final MusicRepository mRepository;

    @Inject
    public GetMusicsByAlbumIdUseCase(MusicRepository repository) {
        mRepository = repository;
    }

    public LiveData<Resource<List<Music>>> execute(String albumId) {
        return mRepository.getMusicsByAlbumId(albumId);
    }

}
