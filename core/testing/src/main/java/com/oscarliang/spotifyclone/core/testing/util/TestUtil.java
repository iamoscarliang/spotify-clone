package com.oscarliang.spotifyclone.core.testing.util;

import static java.util.Collections.emptyList;

import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.model.Artist;
import com.oscarliang.spotifyclone.core.model.Category;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.model.RecentSearch;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static final String UNKNOWN_ID = "-1";
    public static final String UNKNOWN_TITLE = "Unknown";
    public static final String UNKNOWN_URL = "/Unknown";

    public static Album createAlbum(String id, String title) {
        return new Album(id, title, "", "", "", "");
    }

    public static List<Album> createAlbums(int count, String id, String title) {
        List<Album> albums = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            albums.add(createAlbum(id + i, title + i));
        }
        return albums;
    }

    public static Artist createArtist(String id, String name) {
        return new Artist(id, name, "");
    }

    public static List<Artist> createArtists(int count, String id, String name) {
        List<Artist> artists = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            artists.add(createArtist(id + i, name + i));
        }
        return artists;
    }

    public static Category createCategory(String id, String name) {
        return new Category(id, name, "", "");
    }

    public static List<Category> createCategories(int count, String id, String name) {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            categories.add(createCategory(id + i, name + i));
        }
        return categories;
    }

    public static Music createMusic(String id, String title) {
        return new Music(id, title, "", "", "", "", "", emptyList());
    }

    public static Music createMusic(String id, String title, String imageUrl) {
        return new Music(id, title, "", imageUrl, "", "", "", emptyList());
    }

    public static Music createMusic(String id, String title, String albumId, String artistId) {
        return new Music(id, title, "", "", "", albumId, artistId, emptyList());
    }

    public static List<Music> createMusics(int count, String id, String title) {
        List<Music> musics = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            musics.add(createMusic(id + i, title + i));
        }
        return musics;
    }

    public static List<Music> createMusics(int count, String id, String title, String imageUrl) {
        List<Music> musics = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            musics.add(createMusic(id + i, title + i, imageUrl + i));
        }
        return musics;
    }

    public static Playlist createPlaylist(String id, String name) {
        return new Playlist(id, name, "", "", emptyList());
    }

    public static Playlist createPlaylist(String id, List<String> musicIds) {
        return new Playlist(id, "", "", "", musicIds);
    }

    public static Playlist createPlaylist(String id, String name, List<String> musicIds) {
        return new Playlist(id, name, "", "", musicIds);
    }

    public static Playlist createPlaylist(String id, String name, String imageUrl, List<String> musicIds) {
        return new Playlist(id, name, imageUrl, "", musicIds);
    }

    public static List<Playlist> createPlaylists(int count, String id, String name) {
        List<Playlist> playlists = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            playlists.add(createPlaylist(id + i, name + i));
        }
        return playlists;
    }

    public static RecentSearch createRecentSearch(String query, long queriedTime) {
        return new RecentSearch(query, queriedTime);
    }

    public static List<RecentSearch> createRecentSearches(int count, String query, long queriedTime) {
        List<RecentSearch> recentSearches = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            recentSearches.add(createRecentSearch(query + i, queriedTime + i));
        }
        return recentSearches;
    }

}