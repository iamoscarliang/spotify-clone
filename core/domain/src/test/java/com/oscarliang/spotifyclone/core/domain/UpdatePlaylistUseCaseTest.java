package com.oscarliang.spotifyclone.core.domain;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.testing.util.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class UpdatePlaylistUseCaseTest {

    private MusicRepository musicRepository;
    private PlaylistRepository playlistRepository;
    private UpdatePlaylistUseCase useCase;

    @Before
    public void setUp() {
        musicRepository = mock(MusicRepository.class);
        playlistRepository = mock(PlaylistRepository.class);
        useCase = new UpdatePlaylistUseCase(musicRepository, playlistRepository);
    }

    @Test
    public void testUpdatePlaylistReorderMusics() {
        when(musicRepository.getCachedMusicsByIds(asList("foo1", "foo0")))
                .thenReturn(Single.just(TestUtil.createMusics(2, "foo", UNKNOWN_TITLE, "bar")));

        Playlist current = TestUtil.createPlaylist(UNKNOWN_ID, "foobar", "bar0", asList("foo0", "foo1"));
        useCase.execute(current, "barfoo", asList("foo1", "foo0")).subscribe();
        Playlist updated = TestUtil.createPlaylist(UNKNOWN_ID, "barfoo", "bar1", asList("foo1", "foo0"));
        verify(playlistRepository).updatePlaylist(updated);
    }

    @Test
    public void testUpdatePlaylistDeleteMusics() {
        when(musicRepository.getCachedMusicsByIds(singletonList("foo0")))
                .thenReturn(Single.just(TestUtil.createMusics(1, "foo", UNKNOWN_TITLE, "bar")));

        Playlist current = TestUtil.createPlaylist(UNKNOWN_ID, "foobar", "bar1", asList("foo1", "foo0"));
        useCase.execute(current, "barfoo", singletonList("foo0")).subscribe();
        Playlist updated = TestUtil.createPlaylist(UNKNOWN_ID, "barfoo", "bar0", singletonList("foo0"));
        verify(playlistRepository).updatePlaylist(updated);
    }

    @Test
    public void testUpdatePlaylistEmptyMusics() {
        when(musicRepository.getCachedMusicsByIds(emptyList())).thenReturn(Single.just(emptyList()));

        Playlist current = TestUtil.createPlaylist(UNKNOWN_ID, "foobar", "bar0", asList("foo0", "foo1"));
        useCase.execute(current, "barfoo", emptyList()).subscribe();
        Playlist updated = TestUtil.createPlaylist(UNKNOWN_ID, "barfoo", "", emptyList());
        verify(playlistRepository).updatePlaylist(updated);
    }

}