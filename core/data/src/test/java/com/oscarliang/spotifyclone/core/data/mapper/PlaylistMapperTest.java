package com.oscarliang.spotifyclone.core.data.mapper;

import static org.junit.Assert.assertEquals;
import static java.util.Collections.singletonList;

import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.network.model.PlaylistEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PlaylistMapperTest {

    @Test
    public void testMapToModel() {
        PlaylistEntity entity = new PlaylistEntity(
                "0",
                "name",
                "imageUrl",
                "userId",
                singletonList("musicId")
        );
        Playlist model = PlaylistMapper.map(entity);

        assertEquals(model.getId(), "0");
        assertEquals(model.getName(), "name");
        assertEquals(model.getImageUrl(), "imageUrl");
        assertEquals(model.getMusicIds().get(0), "musicId");
    }

    @Test
    public void testMapToEntity() {
        Playlist model = new Playlist(
                "0",
                "name",
                "imageUrl",
                "userId",
                singletonList("musicId")
        );
        PlaylistEntity entity = PlaylistMapper.map(model);

        assertEquals(entity.getId(), "0");
        assertEquals(entity.getName(), "name");
        assertEquals(entity.getImageUrl(), "imageUrl");
        assertEquals(entity.getMusicIds().get(0), "musicId");
    }

}