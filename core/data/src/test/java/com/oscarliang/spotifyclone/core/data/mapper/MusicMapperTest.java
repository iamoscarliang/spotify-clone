package com.oscarliang.spotifyclone.core.data.mapper;

import static org.junit.Assert.assertEquals;
import static java.util.Collections.singletonList;

import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.network.model.MusicEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MusicMapperTest {

    @Test
    public void testMapToModel() {
        MusicEntity entity = new MusicEntity(
                "0",
                "title",
                "artist",
                "imageUrl",
                "musicUrl",
                "albumId",
                "artistId",
                singletonList("category")
        );
        Music model = MusicMapper.map(entity);

        assertEquals(model.getId(), "0");
        assertEquals(model.getTitle(), "title");
        assertEquals(model.getArtist(), "artist");
        assertEquals(model.getImageUrl(), "imageUrl");
        assertEquals(model.getMusicUrl(), "musicUrl");
        assertEquals(model.getAlbumId(), "albumId");
        assertEquals(model.getArtistId(), "artistId");
        assertEquals(model.getCategoryIds().get(0), "category");
    }

    @Test
    public void testMapToEntity() {
        Music model = new Music(
                "0",
                "title",
                "artist",
                "imageUrl",
                "musicUrl",
                "albumId",
                "artistId",
                singletonList("category")
        );
        MusicEntity entity = MusicMapper.map(model);

        assertEquals(entity.getId(), "0");
        assertEquals(entity.getTitle(), "title");
        assertEquals(entity.getArtist(), "artist");
        assertEquals(entity.getImageUrl(), "imageUrl");
        assertEquals(entity.getMusicUrl(), "musicUrl");
        assertEquals(entity.getAlbumId(), "albumId");
        assertEquals(entity.getArtistId(), "artistId");
        assertEquals(entity.getCategoryIds().get(0), "category");
    }

}