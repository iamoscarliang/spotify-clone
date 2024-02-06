package com.oscarliang.spotifyclone.domain.usecase.artist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GetArtistByIdUseCaseTest {

    private ArtistRepository mRepository;
    private GetArtistByIdUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(ArtistRepository.class);
        mUseCase = new GetArtistByIdUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo");
        verify(mRepository).getArtistById("foo");
    }

}
