package com.oscarliang.spotifyclone.feature.playlistinfo;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class PlaylistInfoViewModelTest {

    private PlaylistRepository playlistRepository;
    private PlaylistInfoViewModel viewModel;

    @Before
    public void setUp() {
        playlistRepository = mock(PlaylistRepository.class);
        viewModel = new PlaylistInfoViewModel(playlistRepository);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(playlistRepository, never()).getCachedPlaylistById(any());
    }

    @Test
    public void testBlankQuery() {
        viewModel.setPlaylistId("foo");
        viewModel.setPlaylistId("");
        viewModel.getResult().test().assertEmpty();
    }

    @Test
    public void testQueryWithoutObserver() {
        viewModel.setPlaylistId("foo");
        verify(playlistRepository, never()).getCachedPlaylistById(any());
    }

    @Test
    public void testQueryWhenObserved() {
        viewModel.getResult().subscribe();
        viewModel.setPlaylistId("foo");
        verify(playlistRepository).getCachedPlaylistById("foo");
    }

    @Test
    public void testChangeQuery() {
        when(playlistRepository.getCachedPlaylistById(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setPlaylistId("foo");
        viewModel.setPlaylistId("bar");

        verify(playlistRepository).getCachedPlaylistById("foo");
        verify(playlistRepository).getCachedPlaylistById("bar");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSameQuery() {
        Observer<String> observer = mock(Observer.class);
        viewModel.playlistId.subscribe(observer);
        verify(observer, never()).onNext(any());

        viewModel.setPlaylistId("foo");
        verify(observer).onNext("foo");
        reset(observer);

        viewModel.setPlaylistId("foo");
        verify(observer, never()).onNext(any());
        viewModel.setPlaylistId("bar");
        verify(observer).onNext("bar");
    }

    @Test
    public void testSearch() {
        when(playlistRepository.getCachedPlaylistById("foo"))
                .thenReturn(Single.just(createPlaylist("foo", "bar")));

        viewModel.setPlaylistId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(createPlaylist("foo", "bar"))
        );
    }

}