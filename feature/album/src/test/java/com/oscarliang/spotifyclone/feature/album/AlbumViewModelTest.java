package com.oscarliang.spotifyclone.feature.album;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createAlbum;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusics;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.core.util.Pair;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.AlbumRepository;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class AlbumViewModelTest {

    private AlbumRepository albumRepository;
    private MusicRepository musicRepository;
    private AlbumViewModel viewModel;

    @Before
    public void setUp() {
        albumRepository = mock(AlbumRepository.class);
        musicRepository = mock(MusicRepository.class);
        viewModel = new AlbumViewModel(albumRepository, musicRepository);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(albumRepository, never()).getAlbumById(any());
        verify(musicRepository, never()).getMusicsByAlbumId(any());
    }

    @Test
    public void testBlankQuery() {
        viewModel.setAlbumId("foo");
        viewModel.setAlbumId("");
        viewModel.getResult().test().assertEmpty();
    }

    @Test
    public void testQueryWithoutObserver() {
        viewModel.setAlbumId("foo");
        verify(albumRepository, never()).getAlbumById(any());
        verify(musicRepository, never()).getMusicsByAlbumId(any());
    }

    @Test
    public void testQueryWhenObserved() {
        when(albumRepository.getAlbumById(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setAlbumId("foo");
        verify(albumRepository).getAlbumById("foo");
        verify(musicRepository).getMusicsByAlbumId("foo");
    }

    @Test
    public void testChangeQuery() {
        when(albumRepository.getAlbumById(any())).thenReturn(Single.never());
        when(musicRepository.getMusicsByAlbumId(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setAlbumId("foo");
        viewModel.setAlbumId("bar");

        verify(albumRepository).getAlbumById("foo");
        verify(musicRepository).getMusicsByAlbumId("foo");
        verify(albumRepository).getAlbumById("bar");
        verify(musicRepository).getMusicsByAlbumId("bar");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSameQuery() {
        Observer<String> observer = mock(Observer.class);
        viewModel.albumId.subscribe(observer);
        verify(observer, never()).onNext(any());

        viewModel.setAlbumId("foo");
        verify(observer).onNext("foo");
        reset(observer);

        viewModel.setAlbumId("foo");
        verify(observer, never()).onNext(any());
        viewModel.setAlbumId("bar");
        verify(observer).onNext("bar");
    }

    @Test
    public void testSearch() {
        when(albumRepository.getAlbumById("foo"))
                .thenReturn(Single.just(createAlbum("foo", "bar")));
        when(musicRepository.getMusicsByAlbumId("foo"))
                .thenReturn(Single.just(createMusics(10, "foo", "bar")));

        // Test emit success state after loading state
        viewModel.setAlbumId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(
                        Pair.create(
                                createAlbum("foo", "bar"),
                                createMusics(10, "foo", "bar")
                        )
                )
        );
    }

    @Test
    public void testSearchError() {
        when(albumRepository.getAlbumById("foo"))
                .thenReturn(Single.just(createAlbum("foo", "bar")));
        when(musicRepository.getMusicsByAlbumId("foo"))
                .thenReturn(Single.error(new Exception("idk")));

        // Test emit error state after loading state
        viewModel.setAlbumId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.error("idk", null)
        );
    }

    @Test
    public void testRetry() {
        when(albumRepository.getAlbumById(any())).thenReturn(Single.never());
        when(musicRepository.getMusicsByAlbumId(any())).thenReturn(Single.never());

        // Test retry without query
        viewModel.retry();
        verify(albumRepository, never()).getAlbumById(any());
        verify(musicRepository, never()).getMusicsByAlbumId(any());

        // Test retry with query but without observer
        viewModel.setAlbumId("foo");
        viewModel.retry();
        verify(albumRepository, never()).getAlbumById(any());
        verify(musicRepository, never()).getMusicsByAlbumId(any());

        // Test emit current query when subscribed
        viewModel.getResult().subscribe();
        verify(albumRepository).getAlbumById("foo");
        verify(musicRepository).getMusicsByAlbumId("foo");
        reset(albumRepository, musicRepository);

        // Test retry current query
        viewModel.retry();
        verify(albumRepository).getAlbumById("foo");
        verify(musicRepository).getMusicsByAlbumId("foo");
    }

}