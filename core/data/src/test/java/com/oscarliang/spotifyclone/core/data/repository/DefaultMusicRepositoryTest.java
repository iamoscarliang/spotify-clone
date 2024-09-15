package com.oscarliang.spotifyclone.core.data.repository;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusic;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusics;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import com.oscarliang.spotifyclone.core.network.api.MusicService;
import com.oscarliang.spotifyclone.core.network.model.MusicEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class DefaultMusicRepositoryTest {

    private MusicService service;
    private DefaultMusicRepository repository;

    @Before
    public void setUp() {
        service = mock(MusicService.class);
        repository = new DefaultMusicRepository(service);
    }

    @Test
    public void testGetMusicsById() {
        when(service.getMusicsById(eq("foo"), any()))
                .thenReturn(Single.just(createMusicEntity("foo", "bar")));
        repository.getMusicsById("foo").test()
                .assertValue(createMusic("foo", "bar"));
    }

    @Test
    public void testGetMusicsByIdError() {
        when(service.getMusicsById(eq("foo"), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getMusicsById("foo").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetMusicsByIds() {
        when(service.getMusicsByIds(eq(asList("foo0", "foo1", "foo2")), any()))
                .thenReturn(Single.just(createMusicEntities(3, "foo", "bar")));
        repository.getMusicsByIds(asList("foo0", "foo1", "foo2")).test()
                .assertValue(createMusics(3, "foo", "bar"));
    }

    @Test
    public void testGetMusicsByIdsError() {
        when(service.getMusicsByIds(eq(asList("foo0", "foo1", "foo2")), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getMusicsByIds(asList("foo0", "foo1", "foo2")).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetMusicsByAlbumId() {
        when(service.getMusicsByAlbumId(eq("foobar"), any()))
                .thenReturn(Single.just(createMusicEntities(3, "foo", "bar")));
        repository.getMusicsByAlbumId("foobar").test()
                .assertValue(createMusics(3, "foo", "bar"));
    }

    @Test
    public void testGetMusicsByAlbumIdError() {
        when(service.getMusicsByAlbumId(eq("foobar"), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getMusicsByAlbumId("foobar").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetMusicsByCategory() {
        when(service.getMusicsByCategoryId(eq("foobar"), eq(10), any()))
                .thenReturn(Single.just(createMusicEntities(10, "foo", "bar")));
        repository.getMusicsByCategoryId("foobar", 10).test()
                .assertValue(createMusics(10, "foo", "bar"));
    }

    @Test
    public void testGetMusicsByCategoryError() {
        when(service.getMusicsByCategoryId(eq("foobar"), eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getMusicsByCategoryId("foobar", 10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetMusicsByCategoryNextPage() {
        when(service.getMusicsByCategoryIdNextPage(eq("foobar"), eq(10), any()))
                .thenReturn(Single.just(createMusicEntities(10, "foo", "bar")));
        repository.getMusicsByCategoryIdNextPage("foobar", 10).test()
                .assertValue(createMusics(10, "foo", "bar"));
    }

    @Test
    public void testGetMusicsByCategoryNextPageError() {
        when(service.getMusicsByCategoryIdNextPage(eq("foobar"), eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getMusicsByCategoryIdNextPage("foobar", 10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testSearch() {
        when(service.search(eq("foobar"), eq(10), any()))
                .thenReturn(Single.just(createMusicEntities(10, "foo", "bar")));
        repository.search("foobar", 10).test()
                .assertValue(createMusics(10, "foo", "bar"));
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
                .thenReturn(Single.just(createMusicEntities(10, "foo", "bar")));
        repository.searchNextPage("foobar", 10).test()
                .assertValue(createMusics(10, "foo", "bar"));
    }

    @Test
    public void testSearchNextPageError() {
        when(service.searchNextPage(eq("foobar"), eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.searchNextPage("foobar", 10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    private MusicEntity createMusicEntity(String id, String title) {
        return new MusicEntity(id, title, "", "", "", "", "", emptyList());
    }

    private List<MusicEntity> createMusicEntities(int count, String id, String title) {
        List<MusicEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createMusicEntity(id + i, title + i));
        }
        return entities;
    }

}