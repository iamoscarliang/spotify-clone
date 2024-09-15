package com.oscarliang.spotifyclone.core.data.repository;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylists;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Collections.emptyList;

import com.oscarliang.spotifyclone.core.network.api.PlaylistService;
import com.oscarliang.spotifyclone.core.network.model.PlaylistEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class DefaultPlaylistRepositoryTest {

    private PlaylistService service;
    private DefaultPlaylistRepository repository;

    @Before
    public void setUp() {
        service = mock(PlaylistService.class);
        repository = new DefaultPlaylistRepository(service);
    }

    @Test
    public void testGetPlaylistById() {
        when(service.getPlaylistById(eq("foo"), any()))
                .thenReturn(Single.just(createPlaylistEntity("foo", "bar")));
        repository.getPlaylistById("foo").test()
                .assertValue(createPlaylist("foo", "bar"));
    }

    @Test
    public void testGetPlaylistByIdError() {
        when(service.getPlaylistById(eq("foo"), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getPlaylistById("foo").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetPlaylistsByUserId() {
        when(service.getPlaylistsByUserId(eq("foobar"), any()))
                .thenReturn(Observable.just(createPlaylistEntities(10, "foo", "bar")));
        repository.getPlaylistsByUserId("foobar").test()
                .assertValue(createPlaylists(10, "foo", "bar"));
    }

    @Test
    public void testGetPlaylistsByUserIdError() {
        when(service.getPlaylistsByUserId(eq("foobar"), any()))
                .thenReturn(Observable.error(new Exception("idk")));
        repository.getPlaylistsByUserId("foobar").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testCreatePlaylist() {
        when(service.createPlaylist(any())).thenReturn(Completable.never());
        repository.createPlaylist("foo", "bar").subscribe();
        verify(service).createPlaylist(new PlaylistEntity(null, "foo", "", "bar", emptyList()));
    }

    @Test
    public void testCreatePlaylistError() {
        when(service.createPlaylist(any()))
                .thenReturn(Completable.error(new Exception("idk")));
        repository.createPlaylist("foo", "bar").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testDeletePlaylist() {
        when(service.deletePlaylist(any())).thenReturn(Completable.never());
        repository.deletePlaylist("foo").subscribe();
        verify(service).deletePlaylist("foo");
    }

    @Test
    public void testDeletePlaylistError() {
        when(service.deletePlaylist(any()))
                .thenReturn(Completable.error(new Exception("idk")));
        repository.deletePlaylist("foo").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testUpdatePlaylist() {
        when(service.updatePlaylist(any())).thenReturn(Completable.never());
        repository.updatePlaylist(createPlaylist("foo", "bar")).subscribe();
        verify(service).updatePlaylist(createPlaylistEntity("foo", "bar"));
    }

    @Test
    public void testUpdatePlaylistError() {
        when(service.updatePlaylist(any()))
                .thenReturn(Completable.error(new Exception("idk")));
        repository.updatePlaylist(createPlaylist("foo", "bar")).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    private PlaylistEntity createPlaylistEntity(String id, String name) {
        return new PlaylistEntity(id, name, "", "", emptyList());
    }

    private List<PlaylistEntity> createPlaylistEntities(int count, String id, String name) {
        List<PlaylistEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createPlaylistEntity(id + i, name + i));
        }
        return entities;
    }

}