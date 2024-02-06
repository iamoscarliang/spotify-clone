package com.oscarliang.spotifyclone.ui.artist;

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

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.model.Artist;
import com.oscarliang.spotifyclone.domain.usecase.album.GetAlbumsByArtistIdUseCase;
import com.oscarliang.spotifyclone.domain.usecase.artist.GetArtistByIdUseCase;
import com.oscarliang.spotifyclone.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class ArtistViewModelTest {

    private GetArtistByIdUseCase mGetArtistByIdUseCase;
    private GetAlbumsByArtistIdUseCase mGetAlbumsByArtistIdUseCase;
    private ArtistViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mGetArtistByIdUseCase = mock(GetArtistByIdUseCase.class);
        mGetAlbumsByArtistIdUseCase = mock(GetAlbumsByArtistIdUseCase.class);
        mViewModel = new ArtistViewModel(mGetArtistByIdUseCase, mGetAlbumsByArtistIdUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getArtist());
        assertNotNull(mViewModel.getAlbums());
        verify(mGetArtistByIdUseCase, never()).execute(anyString());
        verify(mGetAlbumsByArtistIdUseCase, never()).execute(anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setArtistId("foo");
        verify(mGetArtistByIdUseCase, never()).execute(anyString());
        verify(mGetAlbumsByArtistIdUseCase, never()).execute(anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        mViewModel.getArtist().observeForever(mock(Observer.class));
        mViewModel.getAlbums().observeForever(mock(Observer.class));
        mViewModel.setArtistId("foo");

        verify(mGetArtistByIdUseCase, times(1)).execute(id.capture());
        verify(mGetAlbumsByArtistIdUseCase, times(1)).execute(id.capture());
        assertEquals("foo", id.getValue());
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        mViewModel.getArtist().observeForever(mock(Observer.class));
        mViewModel.getAlbums().observeForever(mock(Observer.class));
        mViewModel.setArtistId("foo");
        mViewModel.setArtistId("bar");

        verify(mGetArtistByIdUseCase, times(2)).execute(id.capture());
        verify(mGetAlbumsByArtistIdUseCase, times(2)).execute(id.capture());
        assertEquals(Arrays.asList("foo", "bar", "foo", "bar"), id.getAllValues());
    }

    @Test
    public void loadAlbums() {
        Observer<Resource<List<Album>>> observer = mock(Observer.class);
        mViewModel.getAlbums().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mGetAlbumsByArtistIdUseCase);
        mViewModel.setArtistId("foo");
        verify(mGetAlbumsByArtistIdUseCase).execute("foo");
    }

    @Test
    public void resetId() {
        Observer<String> observer = mock(Observer.class);
        mViewModel.mArtistId.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setArtistId("foo");
        verify(observer).onChanged("foo");
        reset(observer);
        mViewModel.setArtistId("foo");
        verifyNoMoreInteractions(observer);
        mViewModel.setArtistId("bar");
        verify(observer).onChanged("bar");
    }

    @Test
    public void retry() {
        mViewModel.retry();
        verifyNoMoreInteractions(mGetArtistByIdUseCase);
        mViewModel.setArtistId("foo");
        verifyNoMoreInteractions(mGetArtistByIdUseCase);
        mViewModel.getArtist().observeForever(mock(Observer.class));
        verify(mGetArtistByIdUseCase).execute("foo");
        reset(mGetArtistByIdUseCase);
        mViewModel.retry();
        verify(mGetArtistByIdUseCase).execute("foo");
    }

    @Test
    public void nullArtistId() {
        mViewModel.setArtistId("foo");
        mViewModel.setArtistId(null);
        Observer<Resource<Artist>> observer = mock(Observer.class);
        Observer<Resource<List<Album>>> musicsObserver = mock(Observer.class);
        mViewModel.getArtist().observeForever(observer);
        mViewModel.getAlbums().observeForever(musicsObserver);
        verify(observer).onChanged(null);
        verify(musicsObserver).onChanged(null);
    }

}
