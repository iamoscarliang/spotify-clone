package com.oscarliang.spotifyclone.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.List;
import java.util.Objects;

public class Music implements Parcelable {

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @DocumentId
    public String mId;
    private String mTitle;
    private String mArtist;
    private String mImageUrl;
    private String mMusicUrl;
    private String mAlbumId;
    private String mArtistId;
    private List<String> mCategories;

    public Music(String id,
                 String title,
                 String artist,
                 String imageUrl,
                 String musicUrl,
                 String albumId,
                 String artistId,
                 List<String> categories) {
        mId = id;
        mTitle = title;
        mArtist = artist;
        mImageUrl = imageUrl;
        mMusicUrl = musicUrl;
        mAlbumId = albumId;
        mArtistId = artistId;
        mCategories = categories;
    }

    public Music() {
        // Needed for Firebase
    }

    protected Music(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mArtist = in.readString();
        mImageUrl = in.readString();
        mMusicUrl = in.readString();
        mAlbumId = in.readString();
        mArtistId = in.readString();
        mCategories = in.createStringArrayList();
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getMusicUrl() {
        return mMusicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        mMusicUrl = musicUrl;
    }

    public String getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(String albumId) {
        mAlbumId = albumId;
    }

    public String getArtistId() {
        return mArtistId;
    }

    public void setArtistId(String artistId) {
        mArtistId = artistId;
    }

    public List<String> getCategory() {
        return mCategories;
    }

    public void setCategory(List<String> categories) {
        mCategories = categories;
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
        return Objects.equals(mTitle, music.mTitle)
                && Objects.equals(mAlbumId, music.mAlbumId)
                && Objects.equals(mArtist, music.mArtist)
                && Objects.equals(mImageUrl, music.mImageUrl)
                && Objects.equals(mMusicUrl, music.mMusicUrl)
                && Objects.equals(mCategories, music.mCategories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mArtist);
        parcel.writeString(mImageUrl);
        parcel.writeString(mMusicUrl);
        parcel.writeString(mAlbumId);
        parcel.writeString(mArtistId);
        parcel.writeStringList(mCategories);
    }

}
