package com.oscarliang.spotifyclone.feature.artist;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createAlbums;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createArtist;
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
import com.oscarliang.spotifyclone.core.data.repository.ArtistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class ArtistViewModelTest {

    private ArtistRepository artistRepository;
    private AlbumRepository albumRepository;
    private ArtistViewModel viewModel;

    @Before
    public void setUp() {
        artistRepository = mock(ArtistRepository.class);
        albumRepository = mock(AlbumRepository.class);
        viewModel = new ArtistViewModel(artistRepository, albumRepository);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(artistRepository, never()).getArtistById(any());
        verify(albumRepository, never()).getAlbumsByArtistId(any());
    }

    @Test
    public void testBlankQuery() {
        viewModel.setArtistId("foo");
        viewModel.setArtistId("");
        viewModel.getResult().test().assertEmpty();
    }

    @Test
    public void testQueryWithoutObserver() {
        viewModel.setArtistId("foo");
        verify(artistRepository, never()).getArtistById(any());
        verify(albumRepository, never()).getAlbumsByArtistId(any());
    }

    @Test
    public void testQueryWhenObserved() {
        when(artistRepository.getArtistById(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setArtistId("foo");
        verify(artistRepository).getArtistById("foo");
        verify(albumRepository).getAlbumsByArtistId("foo");
    }

    @Test
    public void testChangeQuery() {
        when(artistRepository.getArtistById(any())).thenReturn(Single.never());
        when(albumRepository.getAlbumsByArtistId(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setArtistId("foo");
        viewModel.setArtistId("bar");

        verify(artistRepository).getArtistById("foo");
        verify(albumRepository).getAlbumsByArtistId("foo");
        verify(artistRepository).getArtistById("bar");
        verify(albumRepository).getAlbumsByArtistId("bar");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSameQuery() {
        Observer<String> observer = mock(Observer.class);
        viewModel.artistId.subscribe(observer);
        verify(observer, never()).onNext(any());

        viewModel.setArtistId("foo");
        verify(observer).onNext("foo");
        reset(observer);

        viewModel.setArtistId("foo");
        verify(observer, never()).onNext(any());
        viewModel.setArtistId("bar");
        verify(observer).onNext("bar");
    }

    @Test
    public void testSearch() {
        when(artistRepository.getArtistById("foo"))
                .thenReturn(Single.just(createArtist("foo", "bar")));
        when(albumRepository.getAlbumsByArtistId("foo"))
                .thenReturn(Single.just(createAlbums(10, "foo", "bar")));

        // Test emit success state after loading state
        viewModel.setArtistId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(
                        Pair.create(
                                createArtist("foo", "bar"),
                                createAlbums(10, "foo", "bar")
                        )
                )
        );
    }

    @Test
    public void testSearchError() {
        when(artistRepository.getArtistById("foo"))
                .thenReturn(Single.just(createArtist("foo", "bar")));
        when(albumRepository.getAlbumsByArtistId("foo"))
                .thenReturn(Single.error(new Exception("idk")));

        // Test emit error state after loading state
        viewModel.setArtistId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.error("idk", null)
        );
    }

    @Test
    public void testRetry() {
        when(artistRepository.getArtistById(any())).thenReturn(Single.never());
        when(albumRepository.getAlbumsByArtistId(any())).thenReturn(Single.never());

        // Test retry without query
        viewModel.retry();
        verify(artistRepository, never()).getArtistById(any());
        verify(albumRepository, never()).getAlbumsByArtistId(any());

        // Test retry with query but without observer
        viewModel.setArtistId("foo");
        viewModel.retry();
        verify(artistRepository, never()).getArtistById(any());
        verify(albumRepository, never()).getAlbumsByArtistId(any());

        // Test emit current query when subscribed
        viewModel.getResult().subscribe();
        verify(artistRepository).getArtistById("foo");
        verify(albumRepository).getAlbumsByArtistId("foo");
        reset(artistRepository, albumRepository);

        // Test retry current query
        viewModel.retry();
        verify(artistRepository).getArtistById("foo");
        verify(albumRepository).getAlbumsByArtistId("foo");
    }

}