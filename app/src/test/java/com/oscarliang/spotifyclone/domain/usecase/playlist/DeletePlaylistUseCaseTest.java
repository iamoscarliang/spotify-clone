package com.oscarliang.spotifyclone.domain.usecase.playlist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class DeletePlaylistUseCaseTest {

    private PlaylistRepository mRepository;
    private DeletePlaylistUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(PlaylistRepository.class);
        mUseCase = new DeletePlaylistUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo",
                TestUtil.createPlaylist("bar", "abc", "cba", Arrays.asList("a", "b", "c")));
        verify(mRepository).deletePlaylist("foo",
                TestUtil.createPlaylist("bar", "abc", "cba", Arrays.asList("a", "b", "c")));
    }

}
