package com.oscarliang.spotifyclone.core.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Album {

    private String id;
    private String title;
    private String artist;
    private String year;
    private String imageUrl;
    private String artistId;

    public Album(
            String id,
            String title,
            String artist,
            String year,
            String imageUrl,
            String artistId
    ) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.imageUrl = imageUrl;
        this.artistId = artistId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Album album = (Album) o;
        return Objects.equals(title, album.title)
                && Objects.equals(artist, album.artist)
                && Objects.equals(year, album.year)
                && Objects.equals(imageUrl, album.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, year, imageUrl, artistId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", year='" + year + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", artistId='" + artistId + '\'' +
                '}';
    }

}