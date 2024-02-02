package com.oscarliang.spotifyclone.util;

import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.Arrays;
import java.util.List;

public class MusicSorter {

    public static List<Music> sort(List<Music> musicsToSort, List<String> musicIds) {
        int musicCount = musicsToSort.size();
        Music[] sortedMusics = new Music[musicCount];
        for (int i = 0; i < musicCount; i++) {
            Music music = musicsToSort.get(i);
            sortedMusics[musicIds.indexOf(music.getId())] = music;
        }
        return Arrays.asList(sortedMusics);
    }

}
