package com.oscarliang.spotifyclone.core.network.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.List;
import java.util.Objects;

public class PlaylistEntity {

    @DocumentId
    public String id;
    private String name;
    private String imageUrl;
    private String userId;
    private List<String> musicIds;

    public PlaylistEntity(
            String id,
            String name,
            String imageUrl,
            String userId,
            List<String> musicIds
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.musicIds = musicIds;
    }

    public PlaylistEntity() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getMusicIds() {
        return musicIds;
    }

    public void setMusicIds(List<String> musics) {
        musicIds = musics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaylistEntity playlist = (PlaylistEntity) o;
        return Objects.equals(name, playlist.name)
                && Objects.equals(imageUrl, playlist.imageUrl)
                && Objects.equals(musicIds, playlist.musicIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl, musicIds);
    }

    @NonNull
    @Override
    public String toString() {
        return "PlaylistEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", musicIds=" + musicIds +
                '}';
    }

}