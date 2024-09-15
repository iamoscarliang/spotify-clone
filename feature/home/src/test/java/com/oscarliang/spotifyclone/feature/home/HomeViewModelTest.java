package com.oscarliang.spotifyclone.feature.home;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createAlbums;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createArtists;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.core.util.Pair;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.AlbumRepository;
import com.oscarliang.spotifyclone.core.data.repository.ArtistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class HomeViewModelTest {

    private AlbumRepository albumRepository;
    private ArtistRepository artistRepository;
    private HomeViewModel viewModel;

    @Before
    public void setUp() {
        albumRepository = mock(AlbumRepository.class);
        artistRepository = mock(ArtistRepository.class);
        viewModel = new HomeViewModel(albumRepository, artistRepository);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(albumRepository, never()).getAllAlbums(anyInt());
        verify(artistRepository, never()).getAllArtists(anyInt());
    }

    @Test
    public void testBlankQuery() {
        viewModel.setQuery(new HomeQuery(10, 10));
        viewModel.setQuery(new HomeQuery(0, 0));
        viewModel.getResult().test().assertEmpty();
    }

    @Test
    public void testQueryWithoutObserver() {
        viewModel.setQuery(new HomeQuery(10, 10));
        verify(albumRepository, never()).getAllAlbums(anyInt());
        verify(artistRepository, never()).getAllArtists(anyInt());
    }

    @Test
    public void testQueryWhenObserved() {
        viewModel.getResult().subscribe();
        viewModel.setQuery(new HomeQuery(5, 10));
        verify(albumRepository).getAllAlbums(5);
        verify(artistRepository).getAllArtists(10);
    }

    @Test
    public void testChangeQuery() {
        when(albumRepository.getAllAlbums(anyInt())).thenReturn(Single.never());
        when(artistRepository.getAllArtists(anyInt())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setQuery(new HomeQuery(5, 10));
        viewModel.setQuery(new HomeQuery(15, 20));

        verify(albumRepository).getAllAlbums(5);
        verify(artistRepository).getAllArtists(10);
        verify(albumRepository).getAllAlbums(15);
        verify(artistRepository).getAllArtists(20);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSameQuery() {
        Observer<HomeQuery> observer = mock(Observer.class);
        viewModel.query.subscribe(observer);
        verify(observer, never()).onNext(any());

        viewModel.setQuery(new HomeQuery(10, 10));
        verify(observer).onNext(new HomeQuery(10, 10));
        reset(observer);

        viewModel.setQuery(new HomeQuery(10, 10));
        verify(observer, never()).onNext(any());
        viewModel.setQuery(new HomeQuery(20, 20));
        verify(observer).onNext(new HomeQuery(20, 20));
    }

    @Test
    public void testSearch() {
        when(albumRepository.getAllAlbums(10))
                .thenReturn(Single.just(createAlbums(10, "foo", "foo")));
        when(artistRepository.getAllArtists(10))
                .thenReturn(Single.just(createArtists(10, "bar", "bar")));

        // Test emit success state after loading state
        viewModel.setQuery(new HomeQuery(10, 10));
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(
                        Pair.create(
                                createAlbums(10, "foo", "foo"),
                                createArtists(10, "bar", "bar")
                        )
                )
        );
    }

    @Test
    public void testSearchError() {
        when(albumRepository.getAllAlbums(10))
                .thenReturn(Single.just(createAlbums(10, "foo", "foo")));
        when(artistRepository.getAllArtists(10))
                .thenReturn(Single.error(new Exception("idk")));

        // Test emit error state after loading state
        viewModel.setQuery(new HomeQuery(10, 10));
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.error("idk", null)
        );
    }

    @Test
    public void testRetry() {
        when(albumRepository.getAllAlbums(anyInt())).thenReturn(Single.never());
        when(artistRepository.getAllArtists(anyInt())).thenReturn(Single.never());

        // Test retry without query
        viewModel.retry();
        verify(albumRepository, never()).getAllAlbums(anyInt());
        verify(artistRepository, never()).getAllArtists(anyInt());

        // Test retry with query but without observer
        viewModel.setQuery(new HomeQuery(10, 10));
        viewModel.retry();
        verify(albumRepository, never()).getAllAlbums(anyInt());
        verify(artistRepository, never()).getAllArtists(anyInt());

        // Test emit current query when subscribed
        viewModel.getResult().subscribe();
        verify(albumRepository).getAllAlbums(10);
        verify(artistRepository).getAllArtists(10);
        reset(albumRepository, artistRepository);

        // Test retry current query
        viewModel.retry();
        verify(albumRepository).getAllAlbums(10);
        verify(artistRepository).getAllArtists(10);
    }

}