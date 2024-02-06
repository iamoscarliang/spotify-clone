package com.oscarliang.spotifyclone.ui.album;

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
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.usecase.album.GetAlbumByIdUseCase;
import com.oscarliang.spotifyclone.domain.usecase.music.GetMusicsByAlbumIdUseCase;
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
public class AlbumViewModelTest {

    private GetAlbumByIdUseCase mGetAlbumByIdUseCase;
    private GetMusicsByAlbumIdUseCase mGetMusicsByAlbumIdUseCase;
    private AlbumViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mGetAlbumByIdUseCase = mock(GetAlbumByIdUseCase.class);
        mGetMusicsByAlbumIdUseCase = mock(GetMusicsByAlbumIdUseCase.class);
        mViewModel = new AlbumViewModel(mGetAlbumByIdUseCase, mGetMusicsByAlbumIdUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getAlbum());
        assertNotNull(mViewModel.getMusics());
        verify(mGetAlbumByIdUseCase, never()).execute(anyString());
        verify(mGetMusicsByAlbumIdUseCase, never()).execute(anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setAlbumId("foo");
        verify(mGetAlbumByIdUseCase, never()).execute(anyString());
        verify(mGetMusicsByAlbumIdUseCase, never()).execute(anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        mViewModel.getAlbum().observeForever(mock(Observer.class));
        mViewModel.getMusics().observeForever(mock(Observer.class));
        mViewModel.setAlbumId("foo");

        verify(mGetAlbumByIdUseCase, times(1)).execute(id.capture());
        verify(mGetMusicsByAlbumIdUseCase, times(1)).execute(id.capture());
        assertEquals("foo", id.getValue());
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        mViewModel.getAlbum().observeForever(mock(Observer.class));
        mViewModel.getMusics().observeForever(mock(Observer.class));
        mViewModel.setAlbumId("foo");
        mViewModel.setAlbumId("bar");

        verify(mGetAlbumByIdUseCase, times(2)).execute(id.capture());
        verify(mGetMusicsByAlbumIdUseCase, times(2)).execute(id.capture());
        assertEquals(Arrays.asList("foo", "bar", "foo", "bar"), id.getAllValues());
    }

    @Test
    public void loadMusics() {
        Observer<Resource<List<Music>>> observer = mock(Observer.class);
        mViewModel.getMusics().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mGetMusicsByAlbumIdUseCase);
        mViewModel.setAlbumId("foo");
        verify(mGetMusicsByAlbumIdUseCase).execute("foo");
    }

    @Test
    public void resetId() {
        Observer<String> observer = mock(Observer.class);
        mViewModel.mAlbumId.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setAlbumId("foo");
        verify(observer).onChanged("foo");
        reset(observer);
        mViewModel.setAlbumId("foo");
        verifyNoMoreInteractions(observer);
        mViewModel.setAlbumId("bar");
        verify(observer).onChanged("bar");
    }

    @Test
    public void retry() {
        mViewModel.retry();
        verifyNoMoreInteractions(mGetAlbumByIdUseCase);
        mViewModel.setAlbumId("foo");
        verifyNoMoreInteractions(mGetAlbumByIdUseCase);
        mViewModel.getAlbum().observeForever(mock(Observer.class));
        verify(mGetAlbumByIdUseCase).execute("foo");
        reset(mGetAlbumByIdUseCase);
        mViewModel.retry();
        verify(mGetAlbumByIdUseCase).execute("foo");
    }

    @Test
    public void nullAlbumId() {
        mViewModel.setAlbumId("foo");
        mViewModel.setAlbumId(null);
        Observer<Resource<Album>> albumObserver = mock(Observer.class);
        Observer<Resource<List<Music>>> musicsObserver = mock(Observer.class);
        mViewModel.getAlbum().observeForever(albumObserver);
        mViewModel.getMusics().observeForever(musicsObserver);
        verify(albumObserver).onChanged(null);
        verify(musicsObserver).onChanged(null);
    }

}
