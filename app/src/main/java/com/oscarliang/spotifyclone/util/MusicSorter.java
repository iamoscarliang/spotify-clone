package com.oscarliang.spotifyclone.util;

import androidx.annotation.NonNull;

import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicSorter {

    @NonNull
    public static List<Music> sort(@NonNull List<Music> musicsToSort, @NonNull List<String> musicIds) {
        int musicCount = musicsToSort.size();
        Map<Integer, Music> sorted = new HashMap<>();
        for (int i = 0; i < musicCount; i++) {
            Music music = musicsToSort.get(i);
            int index = musicIds.indexOf(music.getId());
            if (index != -1) {
                sorted.put(index, music);
            }
        }
        return new ArrayList<>(sorted.values());
    }

}
