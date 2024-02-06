package com.oscarliang.spotifyclone.domain.usecase.artist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GetAllArtistsUseCaseTest {

    private ArtistRepository mRepository;
    private GetAllArtistsUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(ArtistRepository.class);
        mUseCase = new GetAllArtistsUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute(10);
        verify(mRepository).getAllArtists(10);
    }

}
