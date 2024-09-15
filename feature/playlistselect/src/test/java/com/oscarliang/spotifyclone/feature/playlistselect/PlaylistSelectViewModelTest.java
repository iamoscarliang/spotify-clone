package com.oscarliang.spotifyclone.feature.playlistselect;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.domain.AddMusicToPlaylistUseCase;
import com.oscarliang.spotifyclone.core.model.Playlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;

@RunWith(JUnit4.class)
public class PlaylistSelectViewModelTest {

    private PlaylistRepository playlistRepository;
    private AddMusicToPlaylistUseCase addMusicToPlaylistUseCase;
    private PlaylistSelectViewModel viewModel;

    @Before
    public void setUp() {
        playlistRepository = mock(PlaylistRepository.class);
        addMusicToPlaylistUseCase = mock(AddMusicToPlaylistUseCase.class);
        viewModel = new PlaylistSelectViewModel(playlistRepository, addMusicToPlaylistUseCase);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(playlistRepository, never()).getPlaylistsByUserId(any());
    }

    @Test
    public void testBlankQuery() {
        viewModel.setUserId("foo");
        viewModel.setUserId("");
        viewModel.getResult().test().assertEmpty();
    }

    @Test
    public void testQueryWithoutObserver() {
        viewModel.setUserId("foo");
        verify(playlistRepository, never()).getPlaylistsByUserId(any());
    }

    @Test
    public void testQueryWhenObserved() {
        viewModel.getResult().subscribe();
        viewModel.setUserId("foo");
        verify(playlistRepository).getPlaylistsByUserId("foo");
    }

    @Test
    public void testChangeQuery() {
        when(playlistRepository.getPlaylistsByUserId("foo"))
                .thenReturn(Observable.never());
        when(playlistRepository.getPlaylistsByUserId("bar"))
                .thenReturn(Observable.never());

        viewModel.getResult().subscribe();
        viewModel.setUserId("foo");
        viewModel.setUserId("bar");

        verify(playlistRepository).getPlaylistsByUserId("foo");
        verify(playlistRepository).getPlaylistsByUserId("bar");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSameQuery() {
        Observer<String> observer = mock(Observer.class);
        viewModel.userId.subscribe(observer);
        verify(observer, never()).onNext(any());

        viewModel.setUserId("foo");
        verify(observer).onNext("foo");
        reset(observer);

        viewModel.setUserId("foo");
        verify(observer, never()).onNext(any());
        viewModel.setUserId("bar");
        verify(observer).onNext("bar");
    }

    @Test
    public void testSearch() {
        when(playlistRepository.getPlaylistsByUserId("foo"))
                .thenReturn(Observable.just(createPlaylists(10, "foo", "bar")));

        // Test emit success state after loading state
        viewModel.setUserId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(createPlaylists(10, "foo", "bar"))
        );
    }

    @Test
    public void testSearchError() {
        when(playlistRepository.getPlaylistsByUserId("foo"))
                .thenReturn(Observable.error(new Exception("idk")));

        // Test emit error state after loading state
        viewModel.setUserId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.error("idk", null)
        );
    }

    @Test
    public void testRetry() {
        when(playlistRepository.getPlaylistsByUserId("foo"))
                .thenReturn(Observable.never());

        // Test retry without query
        viewModel.retry();
        verify(playlistRepository, never()).getPlaylistsByUserId(any());

        // Test retry with query but without observer
        viewModel.setUserId("foo");
        viewModel.retry();
        verify(playlistRepository, never()).getPlaylistsByUserId(any());

        // Test emit current query when subscribed
        viewModel.getResult().subscribe();
        verify(playlistRepository).getPlaylistsByUserId("foo");
        reset(playlistRepository);

        // Test retry current query
        viewModel.retry();
        verify(playlistRepository).getPlaylistsByUserId("foo");
    }

    @Test
    public void testCreatePlaylist() {
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userId = ArgumentCaptor.forClass(String.class);

        viewModel.setUserId("foo");
        viewModel.createPlaylist("bar");
        verify(playlistRepository).createPlaylist(name.capture(), userId.capture());
        assertEquals("bar", name.getValue());
        assertEquals("foo", userId.getValue());
    }

    @Test
    public void testAddMusicToPlaylist() {
        ArgumentCaptor<Playlist> playlist = ArgumentCaptor.forClass(Playlist.class);
        ArgumentCaptor<String> musicId = ArgumentCaptor.forClass(String.class);

        viewModel.addMusicToPlaylist(createPlaylist("foo", UNKNOWN_TITLE), "bar");
        verify(addMusicToPlaylistUseCase).execute(playlist.capture(), musicId.capture());
        assertEquals("foo", playlist.getValue().getId());
        assertEquals("bar", musicId.getValue());
    }

}