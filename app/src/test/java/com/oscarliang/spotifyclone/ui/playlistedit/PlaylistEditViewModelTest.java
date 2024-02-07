package com.oscarliang.spotifyclone.ui.playlistedit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.usecase.music.GetMusicsByIdsUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.UpdatePlaylistUseCase;
import com.oscarliang.spotifyclone.ui.playlist.PlaylistViewModel;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.TestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class PlaylistEditViewModelTest {

    private GetMusicsByIdsUseCase mGetMusicsByIdsUseCase;
    private UpdatePlaylistUseCase mUpdatePlaylistUseCase;
    private PlaylistEditViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mGetMusicsByIdsUseCase = mock(GetMusicsByIdsUseCase.class);
        mUpdatePlaylistUseCase = mock(UpdatePlaylistUseCase.class);
        mViewModel = new PlaylistEditViewModel(mGetMusicsByIdsUseCase, mUpdatePlaylistUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getPlaylistMusics());
        verify(mGetMusicsByIdsUseCase, never()).execute(any());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        verify(mGetMusicsByIdsUseCase, never()).execute(any());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<List<String>> id = ArgumentCaptor.forClass(List.class);

        mViewModel.getPlaylistMusics().observeForever(mock(Observer.class));
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));

        verify(mGetMusicsByIdsUseCase, times(1)).execute(id.capture());
        assertEquals(Arrays.asList("foo", "bar"), id.getValue());
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<List<String>> id = ArgumentCaptor.forClass(List.class);

        mViewModel.getPlaylistMusics().observeForever(mock(Observer.class));
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        mViewModel.setPlaylistMusics(Arrays.asList("FOO", "BAR"));

        verify(mGetMusicsByIdsUseCase, times(2)).execute(id.capture());
        assertEquals(Arrays.asList(Arrays.asList("foo", "bar"), Arrays.asList("FOO", "BAR")), id.getAllValues());
    }

    @Test
    public void loadMusics() {
        Observer<Resource<List<Music>>> observer = mock(Observer.class);
        mViewModel.getPlaylistMusics().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mGetMusicsByIdsUseCase);
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        verify(mGetMusicsByIdsUseCase).execute(Arrays.asList("foo", "bar"));
    }

    @Test
    public void resetIds() {
        Observer<List<String>> observer = mock(Observer.class);
        mViewModel.mMusicIds.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        verify(observer).onChanged(Arrays.asList("foo", "bar"));
        reset(observer);
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        verifyNoMoreInteractions(observer);
        mViewModel.setPlaylistMusics(Arrays.asList("FOO", "BAR"));
        verify(observer).onChanged(Arrays.asList("FOO", "BAR"));
    }

    @Test
    public void retry() {
        mViewModel.retry();
        verifyNoMoreInteractions(mGetMusicsByIdsUseCase);
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        verifyNoMoreInteractions(mGetMusicsByIdsUseCase);
        mViewModel.getPlaylistMusics().observeForever(mock(Observer.class));
        verify(mGetMusicsByIdsUseCase).execute(Arrays.asList("foo", "bar"));
        reset(mGetMusicsByIdsUseCase);
        mViewModel.retry();
        verify(mGetMusicsByIdsUseCase).execute(Arrays.asList("foo", "bar"));
    }

    @Test
    public void nullMusicIds() {
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        mViewModel.setPlaylistMusics(null);
        Observer<Resource<List<Music>>> observer = mock(Observer.class);
        mViewModel.getPlaylistMusics().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void emptyMusicIds() {
        mViewModel.setPlaylistMusics(Arrays.asList("foo", "bar"));
        mViewModel.setPlaylistMusics(Collections.emptyList());
        Observer<Resource<List<Music>>> observer = mock(Observer.class);
        mViewModel.getPlaylistMusics().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void updatePlaylist() {
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getUpdatePlaylistState().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mUpdatePlaylistUseCase);
        mViewModel.updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "abc"),
                "ABC",
                TestUtil.createMusics(3, "boo"));
        verify(mUpdatePlaylistUseCase).execute(
                "foo",
                TestUtil.createPlaylist("bar", "abc"),
                "ABC",
                TestUtil.createMusics(3, "boo"));
    }

    @Test
    public void basicUpdate() {
        Observer<PlaylistEditViewModel.UpdatePlaylistQuery> observer = mock(Observer.class);
        mViewModel.mUpdatePlaylistQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.updatePlaylist(
                "foo",
                TestUtil.createPlaylist("bar", "abc"),
                "ABC",
                TestUtil.createMusics(3, "boo"));
        verify(observer).onChanged(new PlaylistEditViewModel.UpdatePlaylistQuery(
                "foo",
                TestUtil.createPlaylist("bar", "abc"),
                "ABC",
                TestUtil.createMusics(3, "boo")));
    }

    @Test
    public void updateFromNullUser() {
        mViewModel.updatePlaylist(
                null,
                TestUtil.createPlaylist("foo", "abc"),
                "ABC",
                TestUtil.createMusics(3, "bar"));
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getUpdatePlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void updateNull() {
        mViewModel.updatePlaylist(
                "foo",
                null,
                "ABC",
                TestUtil.createMusics(3, "bar"));
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getUpdatePlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

}
