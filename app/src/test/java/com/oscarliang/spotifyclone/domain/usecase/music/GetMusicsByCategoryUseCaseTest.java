package com.oscarliang.spotifyclone.domain.usecase.music;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.MusicRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GetMusicsByCategoryUseCaseTest {

    private MusicRepository mRepository;
    private GetMusicsByCategoryUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(MusicRepository.class);
        mUseCase = new GetMusicsByCategoryUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo", 10);
        verify(mRepository).getMusicsByCategory("foo", 10);
    }

}
