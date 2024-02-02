package com.oscarliang.spotifyclone.domain.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Objects;

public class Artist {

    @DocumentId
    public String mId;
    private String mName;
    private String mImageUrl;

    public Artist(String id, String name, String imageUrl) {
        mId = id;
        mName = name;
        mImageUrl = imageUrl;
    }

    public Artist() {
        // Needed for Firebase
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Artist artist = (Artist) o;
        return Objects.equals(mName, artist.mName) && Objects.equals(mImageUrl, artist.mImageUrl);
    }

}
