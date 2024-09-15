package com.oscarliang.spotifyclone.core.data.mapper;

import static org.junit.Assert.assertEquals;

import com.oscarliang.spotifyclone.core.model.Artist;
import com.oscarliang.spotifyclone.core.network.model.ArtistEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ArtistMapperTest {

    @Test
    public void testMapToModel() {
        ArtistEntity entity = new ArtistEntity(
                "0",
                "name",
                "imageUrl"
        );
        Artist model = ArtistMapper.map(entity);

        assertEquals(model.getId(), "0");
        assertEquals(model.getName(), "name");
        assertEquals(model.getImageUrl(), "imageUrl");
    }

    @Test
    public void testMapToEntity() {
        Artist model = new Artist(
                "0",
                "name",
                "imageUrl"
        );
        ArtistEntity entity = ArtistMapper.map(model);

        assertEquals(entity.getId(), "0");
        assertEquals(entity.getName(), "name");
        assertEquals(entity.getImageUrl(), "imageUrl");
    }

}