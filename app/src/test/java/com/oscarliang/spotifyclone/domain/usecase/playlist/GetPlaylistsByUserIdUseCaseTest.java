package com.oscarliang.spotifyclone.domain.usecase.playlist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GetPlaylistsByUserIdUseCaseTest {

    private PlaylistRepository mRepository;
    private GetPlaylistsByUserIdUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(PlaylistRepository.class);
        mUseCase = new GetPlaylistsByUserIdUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo");
        verify(mRepository).getPlaylistsByUserId("foo");
    }

}
