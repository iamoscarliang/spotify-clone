package com.oscarliang.spotifyclone.core.data.repository;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createArtist;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createArtists;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.network.api.ArtistService;
import com.oscarliang.spotifyclone.core.network.model.ArtistEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class DefaultArtistRepositoryTest {

    private ArtistService service;
    private DefaultArtistRepository repository;

    @Before
    public void setUp() {
        service = mock(ArtistService.class);
        repository = new DefaultArtistRepository(service);
    }

    @Test
    public void testGetArtistById() {
        when(service.getArtistById(eq("foo"), any()))
                .thenReturn(Single.just(createArtistEntity("foo", "bar")));
        repository.getArtistById("foo").test()
                .assertValue(createArtist("foo", "bar"));
    }

    @Test
    public void testGetArtistByIdError() {
        when(service.getArtistById(eq("foo"), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getArtistById("foo").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetAllArtists() {
        when(service.getAllArtists(eq(10), any()))
                .thenReturn(Single.just(createArtistEntities(10, "foo", "bar")));
        repository.getAllArtists(10).test()
                .assertValue(createArtists(10, "foo", "bar"));
    }

    @Test
    public void testGetAllArtistsError() {
        when(service.getAllArtists(eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getAllArtists(10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testSearch() {
        when(service.search(eq("foobar"), eq(10), any()))
                .thenReturn(Single.just(createArtistEntities(10, "foo", "bar")));
        repository.search("foobar", 10).test()
                .assertValue(createArtists(10, "foo", "bar"));
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
                .thenReturn(Single.just(createArtistEntities(10, "foo", "bar")));
        repository.searchNextPage("foobar", 10).test()
                .assertValue(createArtists(10, "foo", "bar"));
    }

    @Test
    public void testSearchNextPageError() {
        when(service.searchNextPage(eq("foobar"), eq(10), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.searchNextPage("foobar", 10).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    private ArtistEntity createArtistEntity(String id, String name) {
        return new ArtistEntity(id, name, "");
    }

    private List<ArtistEntity> createArtistEntities(int count, String id, String name) {
        List<ArtistEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createArtistEntity(id + i, name + i));
        }
        return entities;
    }

}