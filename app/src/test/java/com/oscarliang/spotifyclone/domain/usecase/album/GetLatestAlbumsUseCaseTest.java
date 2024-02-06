package com.oscarliang.spotifyclone.domain.usecase.album;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.AlbumRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GetLatestAlbumsUseCaseTest {

    private AlbumRepository mRepository;
    private GetLatestAlbumsUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(AlbumRepository.class);
        mUseCase = new GetLatestAlbumsUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute(10);
        verify(mRepository).getLatestAlbums(10);
    }

}
