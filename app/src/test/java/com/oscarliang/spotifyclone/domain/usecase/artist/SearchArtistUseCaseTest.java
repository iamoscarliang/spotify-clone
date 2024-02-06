package com.oscarliang.spotifyclone.domain.usecase.artist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SearchArtistUseCaseTest {

    private ArtistRepository mRepository;
    private SearchArtistUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(ArtistRepository.class);
        mUseCase = new SearchArtistUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo", 10);
        verify(mRepository).search("foo", 10);
    }

}
