package com.oscarliang.spotifyclone.core.data.mapper;

import com.oscarliang.spotifyclone.core.model.Artist;
import com.oscarliang.spotifyclone.core.network.model.ArtistEntity;

public class ArtistMapper {

    public static Artist map(ArtistEntity entity) {
        return new Artist(
                entity.getId(),
                entity.getName(),
                entity.getImageUrl()
        );
    }

    public static ArtistEntity map(Artist model) {
        return new ArtistEntity(
                model.getId(),
                model.getName(),
                model.getImageUrl()
        );
    }

}