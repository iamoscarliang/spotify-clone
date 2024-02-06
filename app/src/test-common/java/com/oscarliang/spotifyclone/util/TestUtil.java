package com.oscarliang.spotifyclone.util;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static List<Music> createMusics(int count, String id) {
        List<Music> musics = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            musics.add(createMusic(id + i));
        }
        return musics;
    }

    public static List<Music> createMusics(int count, String id, String imageUrl) {
        List<Music> musics = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            musics.add(createMusic(id + i, imageUrl + i));
        }
        return musics;
    }

    public static Music createMusic(String id) {
        return new Music(id, null, null, null, null, null, null, null);
    }

    public static Music createMusic(String id, String imageUrl) {
        return new Music(id, null, null, imageUrl, null, null, null, null);
    }

    public static Playlist createPlaylist(String id, String name) {
        return new Playlist(id, name, null, null);
    }

    public static Playlist createPlaylist(String id, String name, String imageUrl, List<String> musicIds) {
        return new Playlist(id, name, imageUrl, musicIds);
    }

}
