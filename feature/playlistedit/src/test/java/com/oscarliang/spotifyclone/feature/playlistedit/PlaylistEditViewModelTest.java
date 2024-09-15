package com.oscarliang.spotifyclone.feature.playlistedit;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusics;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;

import androidx.core.util.Pair;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.domain.UpdatePlaylistUseCase;
import com.oscarliang.spotifyclone.core.model.Playlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class PlaylistEditViewModelTest {

    private PlaylistRepository playlistRepository;
    private MusicRepository musicRepository;
    private UpdatePlaylistUseCase updatePlaylistUseCase;
    private PlaylistEditViewModel viewModel;

    @Before
    public void setUp() {
        playlistRepository = mock(PlaylistRepository.class);
        musicRepository = mock(MusicRepository.class);
        updatePlaylistUseCase = mock(UpdatePlaylistUseCase.class);
        viewModel = new PlaylistEditViewModel(playlistRepository, musicRepository, updatePlaylistUseCase);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(playlistRepository, never()).getPlaylistById(any());
        verify(musicRepository, never()).getMusicsByIds(any());
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
        verify(playlistRepository, never()).getPlaylistById(any());
        verify(musicRepository, never()).getMusicsByIds(any());
    }

    @Test
    public void testQueryWhenObserved() {
        when(playlistRepository.getPlaylistById("foo"))
                .thenReturn(Single.just(createPlaylist("foo", asList("bar0", "bar1"))));

        viewModel.getResult().subscribe();
        viewModel.setPlaylistId("foo");
        verify(playlistRepository).getPlaylistById("foo");
        verify(musicRepository).getMusicsByIds(asList("bar0", "bar1"));
    }

    @Test
    public void testChangeQuery() {
        when(playlistRepository.getPlaylistById("foo"))
                .thenReturn(Single.just(createPlaylist("foo", asList("foo0", "foo1"))));
        when(playlistRepository.getPlaylistById("bar"))
                .thenReturn(Single.just(createPlaylist("bar", asList("bar0", "bar1"))));
        when(musicRepository.getMusicsByIds(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setPlaylistId("foo");
        viewModel.setPlaylistId("bar");

        verify(playlistRepository).getPlaylistById("foo");
        verify(musicRepository).getMusicsByIds(asList("foo0", "foo1"));
        verify(playlistRepository).getPlaylistById("bar");
        verify(musicRepository).getMusicsByIds(asList("bar0", "bar1"));
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
        when(playlistRepository.getPlaylistById("foo"))
                .thenReturn(Single.just(createPlaylist("foo", asList("foo0", "foo1"))));
        when(musicRepository.getMusicsByIds(asList("foo0", "foo1")))
                .thenReturn(Single.just(createMusics(2, "foo", "bar")));

        // Test emit success state after loading state
        viewModel.setPlaylistId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(
                        Pair.create(
                                createPlaylist("foo", asList("foo0", "foo1")),
                                createMusics(2, "foo", "bar")
                        )
                )
        );
    }

    @Test
    public void testSearchError() {
        when(playlistRepository.getPlaylistById("foo"))
                .thenReturn(Single.just(createPlaylist("foo", asList("foo0", "foo1"))));
        when(musicRepository.getMusicsByIds(asList("foo0", "foo1")))
                .thenReturn(Single.error(new Exception("idk")));

        // Test emit error state after loading state
        viewModel.setPlaylistId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.error("idk", null)
        );
    }

    @Test
    public void testRetry() {
        when(playlistRepository.getPlaylistById("foo"))
                .thenReturn(Single.just(createPlaylist("foo", asList("foo0", "foo1"))));
        when(musicRepository.getMusicsByIds(any())).thenReturn(Single.never());

        // Test retry without playlistId
        viewModel.retry();
        verify(playlistRepository, never()).getPlaylistById(any());
        verify(musicRepository, never()).getMusicsByIds(any());

        // Test retry with playlistId but without observer
        viewModel.setPlaylistId("foo");
        viewModel.retry();
        verify(playlistRepository, never()).getPlaylistById(any());
        verify(musicRepository, never()).getMusicsByIds(any());

        // Test emit current playlistId when subscribed
        viewModel.getResult().subscribe();
        verify(playlistRepository).getPlaylistById("foo");
        verify(musicRepository).getMusicsByIds(asList("foo0", "foo1"));
        reset(playlistRepository, musicRepository);

        // Test retry current playlistId
        when(playlistRepository.getPlaylistById("foo"))
                .thenReturn(Single.just(createPlaylist("foo", asList("foo0", "foo1"))));
        viewModel.retry();
        verify(playlistRepository).getPlaylistById("foo");
        verify(musicRepository).getMusicsByIds(asList("foo0", "foo1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdatePlaylist() {
        ArgumentCaptor<Playlist> playlist = ArgumentCaptor.forClass(Playlist.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<String>> musicIds = ArgumentCaptor.forClass(List.class);

        viewModel.updatePlaylist(
                createPlaylist(UNKNOWN_ID, "foo"),
                "bar",
                asList("foo0", "foo1")
        );
        verify(updatePlaylistUseCase).execute(playlist.capture(), name.capture(), musicIds.capture());
        assertEquals("foo", playlist.getValue().getName());
        assertEquals("bar", name.getValue());
        assertEquals(2, musicIds.getValue().size());
    }

}