package com.oscarliang.spotifyclone.ui.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
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
import com.oscarliang.spotifyclone.domain.usecase.album.GetAllAlbumsUseCase;
import com.oscarliang.spotifyclone.domain.usecase.album.GetLatestAlbumsUseCase;
import com.oscarliang.spotifyclone.domain.usecase.artist.GetAllArtistsUseCase;
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
public class HomeViewModelTest {

    private GetLatestAlbumsUseCase mGetLatestAlbumsUseCase;
    private GetAllAlbumsUseCase mGetAllAlbumsUseCase;
    private GetAllArtistsUseCase mGetAllArtistsUseCase;
    private HomeViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mGetLatestAlbumsUseCase = mock(GetLatestAlbumsUseCase.class);
        mGetAllAlbumsUseCase = mock(GetAllAlbumsUseCase.class);
        mGetAllArtistsUseCase = mock(GetAllArtistsUseCase.class);
        mViewModel = new HomeViewModel(mGetLatestAlbumsUseCase, mGetAllAlbumsUseCase, mGetAllArtistsUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getLatestAlbums());
        assertNotNull(mViewModel.getAllAlbums());
        assertNotNull(mViewModel.getAllArtists());
        verify(mGetLatestAlbumsUseCase, never()).execute(anyInt());
        verify(mGetAllAlbumsUseCase, never()).execute(anyInt());
        verify(mGetAllArtistsUseCase, never()).execute(anyInt());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setLatest(4);
        mViewModel.setAll(10);
        verify(mGetLatestAlbumsUseCase, never()).execute(anyInt());
        verify(mGetAllAlbumsUseCase, never()).execute(anyInt());
        verify(mGetAllArtistsUseCase, never()).execute(anyInt());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<Integer> maxResult = ArgumentCaptor.forClass(Integer.class);

        mViewModel.getLatestAlbums().observeForever(mock(Observer.class));
        mViewModel.getAllAlbums().observeForever(mock(Observer.class));
        mViewModel.getAllArtists().observeForever(mock(Observer.class));
        mViewModel.setLatest(4);
        mViewModel.setAll(10);

        verify(mGetLatestAlbumsUseCase, times(1)).execute(maxResult.capture());
        verify(mGetAllAlbumsUseCase, times(1)).execute(maxResult.capture());
        verify(mGetAllArtistsUseCase, times(1)).execute(maxResult.capture());
        assertEquals(Arrays.asList(4, 10, 10), maxResult.getAllValues());
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<Integer> maxResult = ArgumentCaptor.forClass(Integer.class);

        mViewModel.getLatestAlbums().observeForever(mock(Observer.class));
        mViewModel.getAllAlbums().observeForever(mock(Observer.class));
        mViewModel.getAllArtists().observeForever(mock(Observer.class));
        mViewModel.setLatest(4);
        mViewModel.setAll(10);
        mViewModel.setLatest(8);
        mViewModel.setAll(20);

        verify(mGetLatestAlbumsUseCase, times(2)).execute(maxResult.capture());
        verify(mGetAllAlbumsUseCase, times(2)).execute(maxResult.capture());
        verify(mGetAllArtistsUseCase, times(2)).execute(maxResult.capture());
        assertEquals(Arrays.asList(4, 8, 10, 20, 10, 20), maxResult.getAllValues());
    }

    @Test
    public void loadLatest() {
        Observer<Resource<List<Album>>> observer = mock(Observer.class);
        mViewModel.getLatestAlbums().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mGetLatestAlbumsUseCase);
        mViewModel.setLatest(4);
        verify(mGetLatestAlbumsUseCase).execute(4);
    }

    @Test
    public void loadAll() {
        Observer<Resource<List<Album>>> albumsObserver = mock(Observer.class);
        Observer<Resource<List<Artist>>> artistsObserver = mock(Observer.class);
        mViewModel.getAllAlbums().observeForever(albumsObserver);
        mViewModel.getAllArtists().observeForever(artistsObserver);
        verifyNoMoreInteractions(albumsObserver);
        verifyNoMoreInteractions(artistsObserver);
        verifyNoMoreInteractions(mGetAllAlbumsUseCase);
        verifyNoMoreInteractions(mGetAllArtistsUseCase);
        mViewModel.setAll(10);
        verify(mGetAllAlbumsUseCase).execute(10);
        verify(mGetAllArtistsUseCase).execute(10);
    }

    @Test
    public void resetResult() {
        Observer<Integer> latestObserver = mock(Observer.class);
        Observer<Integer> allObserver = mock(Observer.class);
        mViewModel.mLatestMaxResult.observeForever(latestObserver);
        mViewModel.mAllMaxResult.observeForever(allObserver);
        verifyNoMoreInteractions(latestObserver);
        verifyNoMoreInteractions(allObserver);
        mViewModel.setLatest(4);
        mViewModel.setAll(10);
        verify(latestObserver).onChanged(4);
        verify(allObserver).onChanged(10);
        reset(latestObserver);
        reset(allObserver);
        mViewModel.setLatest(4);
        mViewModel.setAll(10);
        verifyNoMoreInteractions(latestObserver);
        verifyNoMoreInteractions(allObserver);
        mViewModel.setLatest(8);
        mViewModel.setAll(20);
        verify(latestObserver).onChanged(8);
        verify(allObserver).onChanged(20);
    }

    @Test
    public void refresh() {
        mViewModel.refresh();
        verifyNoMoreInteractions(mGetLatestAlbumsUseCase);
        verifyNoMoreInteractions(mGetAllAlbumsUseCase);
        verifyNoMoreInteractions(mGetAllArtistsUseCase);
        mViewModel.setLatest(4);
        mViewModel.setAll(10);
        mViewModel.refresh();
        verifyNoMoreInteractions(mGetLatestAlbumsUseCase);
        verifyNoMoreInteractions(mGetAllAlbumsUseCase);
        verifyNoMoreInteractions(mGetAllArtistsUseCase);
        mViewModel.getLatestAlbums().observeForever(mock(Observer.class));
        mViewModel.getAllAlbums().observeForever(mock(Observer.class));
        mViewModel.getAllArtists().observeForever(mock(Observer.class));
        verify(mGetLatestAlbumsUseCase).execute(4);
        verify(mGetAllAlbumsUseCase).execute(10);
        verify(mGetAllArtistsUseCase).execute(10);
        reset(mGetLatestAlbumsUseCase);
        reset(mGetAllAlbumsUseCase);
        reset(mGetAllArtistsUseCase);
        mViewModel.refresh();
        verify(mGetLatestAlbumsUseCase).execute(4);
        verify(mGetAllAlbumsUseCase).execute(10);
        verify(mGetAllArtistsUseCase).execute(10);
    }

    @Test
    public void emptySearch() {
        mViewModel.setLatest(4);
        mViewModel.setAll(10);
        mViewModel.setLatest(0);
        mViewModel.setAll(0);
        Observer<Resource<List<Album>>> latestObserver = mock(Observer.class);
        Observer<Resource<List<Album>>> allAlbumObserver = mock(Observer.class);
        Observer<Resource<List<Artist>>> allArtistObserver = mock(Observer.class);
        mViewModel.getLatestAlbums().observeForever(latestObserver);
        mViewModel.getAllAlbums().observeForever(allAlbumObserver);
        mViewModel.getAllArtists().observeForever(allArtistObserver);
        verify(latestObserver).onChanged(null);
        verify(allAlbumObserver).onChanged(null);
        verify(allArtistObserver).onChanged(null);
    }

}
