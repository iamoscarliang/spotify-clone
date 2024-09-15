package com.oscarliang.spotifyclone.core.network.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.Objects;

public class ArtistEntity {

    @DocumentId
    public String id;
    private String name;
    private String imageUrl;

    public ArtistEntity(
            String id,
            String name,
            String imageUrl
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public ArtistEntity() {
        // Needed for Firebase
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArtistEntity artist = (ArtistEntity) o;
        return Objects.equals(name, artist.name)
                && Objects.equals(imageUrl, artist.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "ArtistEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

}