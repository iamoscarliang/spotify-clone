package com.oscarliang.spotifyclone.core.data.mapper;

import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.network.model.MusicEntity;

public class MusicMapper {

    public static Music map(MusicEntity entity) {
        return new Music(
                entity.getId(),
                entity.getTitle(),
                entity.getArtist(),
                entity.getImageUrl(),
                entity.getMusicUrl(),
                entity.getAlbumId(),
                entity.getArtistId(),
                entity.getCategoryIds()
        );
    }

    public static MusicEntity map(Music model) {
        return new MusicEntity(
                model.getId(),
                model.getTitle(),
                model.getArtist(),
                model.getImageUrl(),
                model.getMusicUrl(),
                model.getAlbumId(),
                model.getArtistId(),
                model.getCategoryIds()
        );
    }

}