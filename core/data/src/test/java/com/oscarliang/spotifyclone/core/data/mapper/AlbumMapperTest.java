package com.oscarliang.spotifyclone.core.data.mapper;

import static org.junit.Assert.assertEquals;

import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.network.model.AlbumEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AlbumMapperTest {

    @Test
    public void testMapToModel() {
        AlbumEntity entity = new AlbumEntity(
                "0",
                "title",
                "artist",
                "year",
                "imageUrl",
                "artistId"
        );
        Album model = AlbumMapper.map(entity);

        assertEquals(model.getId(), "0");
        assertEquals(model.getTitle(), "title");
        assertEquals(model.getArtist(), "artist");
        assertEquals(model.getYear(), "year");
        assertEquals(model.getImageUrl(), "imageUrl");
        assertEquals(model.getArtistId(), "artistId");
    }

    @Test
    public void testMapToEntity() {
        Album model = new Album(
                "0",
                "title",
                "artist",
                "year",
                "imageUrl",
                "artistId"
        );
        AlbumEntity entity = AlbumMapper.map(model);

        assertEquals(entity.getId(), "0");
        assertEquals(entity.getTitle(), "title");
        assertEquals(entity.getArtist(), "artist");
        assertEquals(entity.getYear(), "year");
        assertEquals(entity.getImageUrl(), "imageUrl");
        assertEquals(entity.getArtistId(), "artistId");
    }

}