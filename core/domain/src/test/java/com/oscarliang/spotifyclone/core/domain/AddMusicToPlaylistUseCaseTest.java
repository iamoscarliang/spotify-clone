package com.oscarliang.spotifyclone.core.domain;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_URL;
import static org.mockito.ArgumentMatchers.any;
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
public class AddMusicToPlaylistUseCaseTest {

    private MusicRepository musicRepository;
    private PlaylistRepository playlistRepository;
    private AddMusicToPlaylistUseCase useCase;

    @Before
    public void setUp() {
        musicRepository = mock(MusicRepository.class);
        playlistRepository = mock(PlaylistRepository.class);
        useCase = new AddMusicToPlaylistUseCase(musicRepository, playlistRepository);
    }

    @Test
    public void testAddMusicToPlaylistWithImage() {
        when(musicRepository.getCachedMusicsById("foo2"))
                .thenReturn(Single.just(TestUtil.createMusic("foo2", UNKNOWN_TITLE, "bar")));

        Playlist current = TestUtil.createPlaylist(UNKNOWN_ID, UNKNOWN_TITLE, UNKNOWN_URL, asList("foo0", "foo1"));
        useCase.execute(current, "foo2").subscribe();
        Playlist updated = TestUtil.createPlaylist(UNKNOWN_ID, UNKNOWN_TITLE, UNKNOWN_URL, asList("foo0", "foo1", "foo2"));
        verify(playlistRepository).updatePlaylist(updated);
    }

    @Test
    public void testAddMusicToPlaylistWithoutImage() {
        when(musicRepository.getCachedMusicsById(any()))
                .thenReturn(Single.just(TestUtil.createMusic("foo", UNKNOWN_TITLE, "bar")));

        Playlist current = TestUtil.createPlaylist(UNKNOWN_ID, UNKNOWN_TITLE, "", emptyList());
        useCase.execute(current, "foo").subscribe();
        Playlist updated = TestUtil.createPlaylist(UNKNOWN_ID, UNKNOWN_TITLE, "bar", singletonList("foo"));
        verify(playlistRepository).updatePlaylist(updated);
    }

}