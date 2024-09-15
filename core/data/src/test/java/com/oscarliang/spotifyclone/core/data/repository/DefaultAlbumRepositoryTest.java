package com.oscarliang.spotifyclone.core.data.repository;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createAlbum;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createAlbums;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.network.api.AlbumService;
import com.oscarliang.spotifyclone.core.network.model.AlbumEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class DefaultAlbumRepositoryTest {

    private AlbumService service;
    private DefaultAlbumRepository repository;

    @Before
    public void setUp() {
        service = mock(AlbumService.class);
        repository = new DefaultAlbumRepository(service);
    }

    @Test
    public void testGetAlbumById() {
        when(service.getAlbumById(eq("foo"), any()))
                .thenReturn(Single.just(createAlbumEntity("foo", "bar")));
        repository.getAlbumById("foo").test()
                .assertValue(createAlbum("foo", "bar"));
    }

    @Test
    public void testGetAlbumByIdError() {
        when(service.getAlbumById(eq("foo"), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getAlbumById("foo").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetAlbumsByArtistId() {
        when(service.getAlbumsByArtistId(eq("foobar"), any()))
                .thenReturn(Single.just(createAlbumEntities(3, "foo", "bar")));
        repository.getAlbumsByArtistId("foobar").test()
                .assertValue(createAlbums(3, "foo", "bar"));
    }

    @Test
    public void testGetAlbumsByArtistIdError() {
        when(service.getAlbumsByArtistId(eq("foobar"), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getAlbumsByArtistId("foobar").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetAllAlbums() {
        when(service.getAllAlbums(eq(10), any()))
                .thenReturn(Single.just(createAlbumEntities(10, "foo", "bar")));
        repository.getAllAlbums(10).test()
                .assertValue(createAlbums(10, "foo", "bar"));
    }

    @Test
    public void testGetAllAlbumsError() {
        when(service.getAllAlbums(eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getAllAlbums(10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testSearch() {
        when(service.search(eq("foobar"), eq(10), any()))
                .thenReturn(Single.just(createAlbumEntities(10, "foo", "bar")));
        repository.search("foobar", 10).test()
                .assertValue(createAlbums(10, "foo", "bar"));
    }

    @Test
    public void testSearchError() {
        when(service.search(eq("foobar"), eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.search("foobar", 10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testSearchNextPage() {
        when(service.searchNextPage(eq("foobar"), eq(10), any()))
                .thenReturn(Single.just(createAlbumEntities(10, "foo", "bar")));
        repository.searchNextPage("foobar", 10).test()
                .assertValue(createAlbums(10, "foo", "bar"));
    }

    @Test
    public void testSearchNextPageError() {
        when(service.searchNextPage(eq("foobar"), eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.searchNextPage("foobar", 10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    private AlbumEntity createAlbumEntity(String id, String title) {
        return new AlbumEntity(id, title, "", "", "", "");
    }

    private List<AlbumEntity> createAlbumEntities(int count, String id, String title) {
        List<AlbumEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createAlbumEntity(id + i, title + i));
        }
        return entities;
    }

}