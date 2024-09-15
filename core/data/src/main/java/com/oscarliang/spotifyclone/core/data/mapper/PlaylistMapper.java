package com.oscarliang.spotifyclone.core.data.mapper;

import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.network.model.PlaylistEntity;

public class PlaylistMapper {

    public static Playlist map(PlaylistEntity entity) {
        return new Playlist(
                entity.getId(),
                entity.getName(),
                entity.getImageUrl(),
                entity.getUserId(),
                entity.getMusicIds()
        );
    }

    public static PlaylistEntity map(Playlist model) {
        return new PlaylistEntity(
                model.getId(),
                model.getName(),
                model.getImageUrl(),
                model.getUserId(),
                model.getMusicIds()
        );
    }

}