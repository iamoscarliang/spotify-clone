package com.oscarliang.spotifyclone.domain.usecase.playlist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RunWith(JUnit4.class)
public class AddMusicToPlaylistUseCaseTest {

    private PlaylistRepository mRepository;
    private AddMusicToPlaylistUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(PlaylistRepository.class);
        mUseCase = new AddMusicToPlaylistUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute(
                "foo",
                TestUtil.createPlaylist("bar", "abc", "cba", new ArrayList<>(Arrays.asList("a", "b", "c"))),
                TestUtil.createMusicWithImage("FOO", "BAR"));
        verify(mRepository).updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "abc", "cba", new ArrayList<>(Arrays.asList("a", "b", "c", "FOO"))));
    }

    @Test
    public void executeEmpty() {
        mUseCase.execute("foo",
                TestUtil.createPlaylist(null, "bar", null, new ArrayList<>()),
                TestUtil.createMusicWithImage("FOO", "BAR"));
        verify(mRepository).updatePlaylist("foo",
                TestUtil.createPlaylist(null, "bar", "BAR", Collections.singletonList("FOO")));
    }

}
