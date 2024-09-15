package com.oscarliang.spotifyclone.core.data.mapper;

import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.network.model.AlbumEntity;

public class AlbumMapper {

    public static Album map(AlbumEntity entity) {
        return new Album(
                entity.getId(),
                entity.getTitle(),
                entity.getArtist(),
                entity.getYear(),
                entity.getImageUrl(),
                entity.getArtistId()
        );
    }

    public static AlbumEntity map(Album model) {
        return new AlbumEntity(
                model.getId(),
                model.getTitle(),
                model.getArtist(),
                model.getYear(),
                model.getImageUrl(),
                model.getArtistId()
        );
    }

}