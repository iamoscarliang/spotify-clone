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
import java.util.Collections;

@RunWith(JUnit4.class)
public class UpdatePlaylistUseCaseTest {

    private PlaylistRepository mRepository;
    private UpdatePlaylistUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(PlaylistRepository.class);
        mUseCase = new UpdatePlaylistUseCase(mRepository);
    }

    @Test
    public void executeAdd() {
        mUseCase.execute(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Arrays.asList("FOO1")),
                "boo",
                TestUtil.createMusicsWithImage(3, "FOO", "BAR"));
        verify(mRepository).updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Arrays.asList("FOO1", "FOO2", "FOO3")));
    }

    @Test
    public void executeAddEmpty() {
        mUseCase.execute(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Collections.emptyList()),
                "boo",
                TestUtil.createMusicsWithImage(3, "FOO", "BAR"));
        verify(mRepository).updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Arrays.asList("FOO1", "FOO2", "FOO3")));
    }

    @Test
    public void executeDelete() {
        mUseCase.execute(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Arrays.asList("FOO1", "FOO2", "FOO3")),
                "boo",
                TestUtil.createMusicsWithImage(2, "FOO", "BAR"));
        verify(mRepository).updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Arrays.asList("FOO1", "FOO2")));
    }

    @Test
    public void executeDeleteEmpty() {
        mUseCase.execute(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Arrays.asList("FOO1", "FOO2", "FOO3")),
                "boo",
                Collections.emptyList());
        verify(mRepository).updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "", Collections.emptyList()));
    }

    @Test
    public void executeReorder() {
        mUseCase.execute(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR2", Arrays.asList("FOO2", "FOO1", "FOO3")),
                "boo",
                TestUtil.createMusicsWithImage(3, "FOO", "BAR"));
        verify(mRepository).updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR1", Arrays.asList("FOO1", "FOO2", "FOO3")));
    }

    @Test
    public void executeRename() {
        mUseCase.execute(
                "foo",
                TestUtil.createPlaylist("bar", "boo", "BAR2", Arrays.asList("FOO2", "FOO1", "FOO3")),
                "BOO",
                TestUtil.createMusicsWithImage(3, "FOO", "BAR"));
        verify(mRepository).updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "BOO", "BAR1", Arrays.asList("FOO1", "FOO2", "FOO3")));
    }

}
