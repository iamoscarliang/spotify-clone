package com.oscarliang.spotifyclone.feature.home;

import androidx.annotation.NonNull;

import java.util.Objects;

public class HomeQuery {

    public final int albumsCount;
    public final int artistsCount;

    public HomeQuery(
            int albumsCount,
            int artistsCount
    ) {
        this.albumsCount = albumsCount;
        this.artistsCount = artistsCount;
    }

    public boolean isEmpty() {
        return albumsCount == 0 || artistsCount == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HomeQuery query = (HomeQuery) o;
        return albumsCount == query.albumsCount
                && artistsCount == query.artistsCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(albumsCount, artistsCount);
    }

    @NonNull
    @Override
    public String toString() {
        return "HomeQuery{" +
                "albumsCount=" + albumsCount +
                ", artistsCount=" + artistsCount +
                '}';
    }

}