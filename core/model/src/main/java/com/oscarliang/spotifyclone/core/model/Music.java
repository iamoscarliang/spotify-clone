package com.oscarliang.spotifyclone.core.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class Music {

    private String id;
    private String title;
    private String artist;
    private String imageUrl;
    private String musicUrl;
    private String albumId;
    private String artistId;
    private List<String> categoryIds;

    public Music(
            String id,
            String title,
            String artist,
            String imageUrl,
            String musicUrl,
            String albumId,
            String artistId,
            List<String> categoryIds
    ) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.musicUrl = musicUrl;
        this.albumId = albumId;
        this.artistId = artistId;
        this.categoryIds = categoryIds;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Music music = (Music) o;
        return Objects.equals(title, music.title)
                && Objects.equals(albumId, music.albumId)
                && Objects.equals(artist, music.artist)
                && Objects.equals(imageUrl, music.imageUrl)
                && Objects.equals(musicUrl, music.musicUrl)
                && Objects.equals(categoryIds, music.categoryIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, imageUrl, musicUrl, albumId, artistId, categoryIds);
    }

    @NonNull
    @Override
    public String toString() {
        return "Music{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", musicUrl='" + musicUrl + '\'' +
                ", albumId='" + albumId + '\'' +
                ", artistId='" + artistId + '\'' +
                ", categoryIds=" + categoryIds +
                '}';
    }

}