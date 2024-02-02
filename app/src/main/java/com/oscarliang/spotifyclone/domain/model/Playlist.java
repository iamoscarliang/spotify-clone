package com.oscarliang.spotifyclone.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.List;
import java.util.Objects;

public class Playlist implements Parcelable {

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @DocumentId
    public String mId;
    private String mName;
    private String mImageUrl;
    private List<String> mMusicIds;

    public Playlist(String id, String name, String imageUrl, List<String> musicIds) {
        mId = id;
        mName = name;
        mImageUrl = imageUrl;
        mMusicIds = musicIds;
    }

    public Playlist() {
        // Needed for Firebase
    }

    protected Playlist(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mImageUrl = in.readString();
        mMusicIds = in.createStringArrayList();
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

    public List<String> getMusicIds() {
        return mMusicIds;
    }

    public void setMusicIds(List<String> musics) {
        mMusicIds = musics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Playlist playlist = (Playlist) o;
        return Objects.equals(mName, playlist.mName)
                && Objects.equals(mImageUrl, playlist.mImageUrl)
                && Objects.equals(mMusicIds, playlist.mMusicIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mName);
        parcel.writeString(mImageUrl);
        parcel.writeStringList(mMusicIds);
    }

}
