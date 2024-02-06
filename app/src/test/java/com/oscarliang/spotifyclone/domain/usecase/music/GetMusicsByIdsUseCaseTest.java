package com.oscarliang.spotifyclone.domain.usecase.music;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.MusicRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class GetMusicsByIdsUseCaseTest {

    private MusicRepository mRepository;
    private GetMusicsByIdsUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(MusicRepository.class);
        mUseCase = new GetMusicsByIdsUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute(Arrays.asList("foo", "bar"));
        verify(mRepository).getMusicsByIds(Arrays.asList("foo", "bar"));
    }

}
