package com.oscarliang.spotifyclone.ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.google.common.util.concurrent.ListenableFuture;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.usecase.playlist.AddMusicToNewPlaylistUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.AddMusicToPlaylistUseCase;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.TestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MainViewModelTest {

    private AddMusicToPlaylistUseCase mAddMusicToPlaylistUseCase;
    private AddMusicToNewPlaylistUseCase mAddMusicToNewPlaylistUseCase;
    private MainViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mAddMusicToNewPlaylistUseCase = mock(AddMusicToNewPlaylistUseCase.class);
        mAddMusicToPlaylistUseCase = mock(AddMusicToPlaylistUseCase.class);
        mViewModel = new MainViewModel(mock(ListenableFuture.class),
                mAddMusicToPlaylistUseCase, mAddMusicToNewPlaylistUseCase);
    }

    @Test
    public void addMusicToPlaylist() {
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddToPlaylistState().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mAddMusicToPlaylistUseCase);
        mViewModel.addToPlaylist(
                "foo",
                TestUtil.createPlaylist("FOO", "BAR"),
                TestUtil.createMusic("bar"));
        verify(mAddMusicToPlaylistUseCase).execute(
                "foo",
                TestUtil.createPlaylist("FOO", "BAR"),
                TestUtil.createMusic("bar"));
    }

    @Test
    public void addMusicToNewPlaylist() {
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddToNewPlaylistState().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mAddMusicToNewPlaylistUseCase);
        mViewModel.addToNewPlaylist("foo", "bar", TestUtil.createMusic("abc"));
        verify(mAddMusicToNewPlaylistUseCase).execute("foo", "bar", TestUtil.createMusic("abc"));
    }

    @Test
    public void basicAddMusicToPlaylist() {
        Observer<MainViewModel.AddToPlaylistQuery> observer = mock(Observer.class);
        mViewModel.mAddToPlaylistQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.addToPlaylist("foo",
                TestUtil.createPlaylist("FOO", "BAR"),
                TestUtil.createMusic("bar"));
        verify(observer).onChanged(new MainViewModel.AddToPlaylistQuery(
                "foo",
                TestUtil.createPlaylist("FOO", "BAR"),
                TestUtil.createMusic("bar")));
    }

    @Test
    public void basicAddMusicToNewPlaylist() {
        Observer<MainViewModel.AddToNewPlaylistQuery> observer = mock(Observer.class);
        mViewModel.mAddToNewPlaylistQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.addToNewPlaylist("foo", "bar", TestUtil.createMusic("abc"));
        verify(observer).onChanged(new MainViewModel.AddToNewPlaylistQuery(
                "foo", "bar", TestUtil.createMusic("abc")));
    }

    @Test
    public void addToNullUser() {
        mViewModel.addToPlaylist(
                null,
                TestUtil.createPlaylist("foo", "bar"),
                TestUtil.createMusic("abc"));
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddToPlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void addNull() {
        mViewModel.addToPlaylist("foo", null, null);
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddToPlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void addEmpty() {
        mViewModel.addToPlaylist(
                "foo",
                TestUtil.createPlaylist("", ""),
                TestUtil.createMusic(""));
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddToPlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

}
