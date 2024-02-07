package com.oscarliang.spotifyclone.ui.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.usecase.playlist.AddNewPlaylistUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.DeletePlaylistUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.GetPlaylistsByUserIdUseCase;
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
import java.util.List;

@RunWith(JUnit4.class)
public class LibraryViewModelTest {

    private GetPlaylistsByUserIdUseCase mGetPlaylistsByUserIdUseCase;
    private AddNewPlaylistUseCase mAddNewPlaylistUseCase;
    private DeletePlaylistUseCase mDeletePlaylistUseCase;
    private LibraryViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mGetPlaylistsByUserIdUseCase = mock(GetPlaylistsByUserIdUseCase.class);
        mAddNewPlaylistUseCase = mock(AddNewPlaylistUseCase.class);
        mDeletePlaylistUseCase = mock(DeletePlaylistUseCase.class);
        mViewModel = new LibraryViewModel(mGetPlaylistsByUserIdUseCase, mAddNewPlaylistUseCase, mDeletePlaylistUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getPlaylists());
        verify(mGetPlaylistsByUserIdUseCase, never()).execute(anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setUser("foo");
        verify(mGetPlaylistsByUserIdUseCase, never()).execute(anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        mViewModel.getPlaylists().observeForever(mock(Observer.class));
        mViewModel.setUser("foo");

        verify(mGetPlaylistsByUserIdUseCase, times(1)).execute(id.capture());
        assertEquals("foo", id.getValue());
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        mViewModel.getPlaylists().observeForever(mock(Observer.class));
        mViewModel.setUser("foo");
        mViewModel.setUser("bar");

        verify(mGetPlaylistsByUserIdUseCase, times(2)).execute(id.capture());
        assertEquals(Arrays.asList("foo", "bar"), id.getAllValues());
    }

    @Test
    public void loadPlaylist() {
        Observer<Resource<List<Playlist>>> observer = mock(Observer.class);
        mViewModel.getPlaylists().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mGetPlaylistsByUserIdUseCase);
        mViewModel.setUser("foo");
        verify(mGetPlaylistsByUserIdUseCase).execute("foo");
    }

    @Test
    public void addPlaylist() {
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddPlaylistState().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mAddNewPlaylistUseCase);
        mViewModel.addPlaylist("foo", "bar");
        verify(mAddNewPlaylistUseCase).execute("foo", "bar");
    }

    @Test
    public void deletePlaylist() {
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getDeletePlaylistState().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mDeletePlaylistUseCase);
        mViewModel.deletePlaylist("foo",
                new Playlist("bar", null, null, null));
        verify(mDeletePlaylistUseCase).execute("foo",
                new Playlist("bar", null, null, null));
    }

    @Test
    public void resetId() {
        Observer<String> observer = mock(Observer.class);
        mViewModel.mUser.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setUser("foo");
        verify(observer).onChanged("foo");
        reset(observer);
        mViewModel.setUser("foo");
        verifyNoMoreInteractions(observer);
        mViewModel.setUser("bar");
        verify(observer).onChanged("bar");
    }

    @Test
    public void refresh() {
        mViewModel.refresh();
        verifyNoMoreInteractions(mGetPlaylistsByUserIdUseCase);
        mViewModel.setUser("foo");
        mViewModel.refresh();
        verifyNoMoreInteractions(mGetPlaylistsByUserIdUseCase);
        mViewModel.getPlaylists().observeForever(mock(Observer.class));
        verify(mGetPlaylistsByUserIdUseCase).execute("foo");
        reset(mGetPlaylistsByUserIdUseCase);
        mViewModel.refresh();
        verify(mGetPlaylistsByUserIdUseCase).execute("foo");
    }

    @Test
    public void nullUserId() {
        mViewModel.setUser("foo");
        mViewModel.setUser(null);
        Observer<Resource<List<Playlist>>> observer = mock(Observer.class);
        mViewModel.getPlaylists().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void basicAdd() {
        Observer<LibraryViewModel.AddNewPlaylistQuery> observer = mock(Observer.class);
        mViewModel.mAddNewPlaylistQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.addPlaylist("foo", "bar");
        verify(observer).onChanged(new LibraryViewModel.AddNewPlaylistQuery("foo", "bar"));
    }

    @Test
    public void addToNullUser() {
        mViewModel.addPlaylist(null, "foo");
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddPlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void addNull() {
        mViewModel.addPlaylist("foo", null);
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddPlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void addEmpty() {
        mViewModel.addPlaylist("foo", "");
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getAddPlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void basicDelete() {
        Observer<LibraryViewModel.DeletePlaylistQuery> observer = mock(Observer.class);
        mViewModel.mDeletePlaylistQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.deletePlaylist("foo",
                TestUtil.createPlaylist("bar", "abc"));
        verify(observer).onChanged(new LibraryViewModel.DeletePlaylistQuery("foo",
                TestUtil.createPlaylist("bar", "abc")));
    }

    @Test
    public void deleteFromNullUser() {
        mViewModel.deletePlaylist(null, TestUtil.createPlaylist("bar", "abc"));
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getDeletePlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void deleteNull() {
        mViewModel.deletePlaylist("foo", null);
        Observer<Event<Resource<Playlist>>> observer = mock(Observer.class);
        mViewModel.getDeletePlaylistState().observeForever(observer);
        verify(observer).onChanged(null);
    }

}
