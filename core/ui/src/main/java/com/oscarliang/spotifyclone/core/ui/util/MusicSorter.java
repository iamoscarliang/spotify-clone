package com.oscarliang.spotifyclone.core.ui.util;

import androidx.annotation.NonNull;

import com.oscarliang.spotifyclone.core.model.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicSorter {

    private MusicSorter() {
    }

    @NonNull
    public static List<Music> sort(
            @NonNull List<Music> musicsToSort,
            @NonNull List<String> musicIds
    ) {
        int count = musicsToSort.size();
        Map<Integer, Music> sorted = new HashMap<>();
        for (int i = 0; i < count; i++) {
            Music music = musicsToSort.get(i);
            int index = musicIds.indexOf(music.getId());
            if (index != -1) {
                sorted.put(index, music);
            }
        }
        return new ArrayList<>(sorted.values());
    }

}