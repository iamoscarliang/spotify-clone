package com.oscarliang.spotifyclone.util;

import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.model.Artist;
import com.oscarliang.spotifyclone.domain.model.Category;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    //--------------------------------------------------------
    // Music
    //--------------------------------------------------------
    public static List<Music> createMusics(int count, String id) {
        List<Music> musics = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            musics.add(createMusic(id + i));
        }
        return musics;
    }

    public static List<Music> createMusics(int count, String id, String title) {
        List<Music> musics = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            musics.add(createMusic(id + i, title + i));
        }
        return musics;
    }

    public static List<Music> createMusicsWithImage(int count, String id, String imageUrl) {
        List<Music> musics = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            musics.add(createMusicWithImage(id + i, imageUrl + i));
        }
        return musics;
    }

    public static Music createMusic(String id) {
        return new Music(id, null, null, null, null, null, null, null);
    }

    public static Music createMusic(String id, String title) {
        return new Music(id, title, null, null, null, null, null, null);
    }

    public static Music createMusic(String id, String title, String artist) {
        return new Music(id, title, artist, null, null, null, null, null);
    }

    public static Music createMusicWithImage(String id, String imageUrl) {
        return new Music(id, null, null, imageUrl, null, null, null, null);
    }
    //========================================================

    //--------------------------------------------------------
    // Album
    //--------------------------------------------------------
    public static List<Album> createAlbums(int count, String id, String title) {
        List<Album> albums = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            albums.add(createAlbum(id + i, title + i));
        }
        return albums;
    }

    public static Album createAlbum(String id, String title) {
        return new Album(id, title, null, null, null, null);
    }

    public static Album createAlbum(String id, String title, String artist, String year) {
        return new Album(id, title, artist, year, null, null);
    }
    //========================================================

    //--------------------------------------------------------
    // Album
    //--------------------------------------------------------
    public static List<Artist> createArtists(int count, String id, String name) {
        List<Artist> artists = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            artists.add(createArtist(id + i, name + i));
        }
        return artists;
    }

    public static Artist createArtist(String id, String name) {
        return new Artist(id, name, null);
    }
    //========================================================

    //--------------------------------------------------------
    // Category
    //--------------------------------------------------------
    public static Category createCategory(String name) {
        return new Category(name, null);
    }
    //========================================================

    //--------------------------------------------------------
    // Playlist
    //--------------------------------------------------------
    public static Playlist createPlaylist(String id, String name) {
        return new Playlist(id, name, null, null);
    }

    public static Playlist createPlaylist(String id, String name, String imageUrl, List<String> musicIds) {
        return new Playlist(id, name, imageUrl, musicIds);
    }
    //========================================================

}
