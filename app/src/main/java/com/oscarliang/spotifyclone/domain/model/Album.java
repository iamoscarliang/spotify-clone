package com.oscarliang.spotifyclone.domain.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Objects;

public class Album {

    @DocumentId
    public String mId;
    private String mTitle;
    private String mArtist;
    private String mYear;
    private String mImageUrl;
    private String mArtistId;

    public Album(String id, String title, String artist, String year, String imageUrl, String artistId) {
        mId = id;
        mTitle = title;
        mArtist = artist;
        mYear = year;
        mImageUrl = imageUrl;
        mArtistId = artistId;
    }

    public Album() {
        // Needed for Firebase
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getArtistId() {
        return mArtistId;
    }

    public void setArtistId(String artistId) {
        mArtistId = artistId;
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
        return Objects.equals(mTitle, album.mTitle)
                && Objects.equals(mArtist, album.mArtist)
                && Objects.equals(mYear, album.mYear)
                && Objects.equals(mImageUrl, album.mImageUrl);
    }

}
