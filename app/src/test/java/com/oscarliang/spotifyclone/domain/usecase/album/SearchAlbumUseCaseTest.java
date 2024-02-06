package com.oscarliang.spotifyclone.domain.usecase.album;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.AlbumRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SearchAlbumUseCaseTest {

    private AlbumRepository mRepository;
    private SearchAlbumUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(AlbumRepository.class);
        mUseCase = new SearchAlbumUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo", 10);
        verify(mRepository).search("foo", 10);
    }

}
