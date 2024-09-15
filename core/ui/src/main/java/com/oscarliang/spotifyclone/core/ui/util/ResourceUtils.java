package com.oscarliang.spotifyclone.core.ui.util;

import com.oscarliang.spotifyclone.core.ui.R;

public class ResourceUtils {

    private ResourceUtils() {
    }

    public static int getPlayResId(boolean isPlaying) {
        return isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
    }

    public static int getFillPlayResId(boolean isPlaying) {
        return isPlaying ? R.drawable.ic_pause_circle : R.drawable.ic_play_circle;
    }

    public static int getShuffleModeResId(boolean shuffleModeEnabled) {
        return shuffleModeEnabled ? R.drawable.ic_shuffle_on : R.drawable.ic_shuffle_off;
    }

    public static int getRepeatModeResId(int repeatMode) {
        switch (repeatMode) {
            case 0:
                return R.drawable.ic_repeat_off;
            case 1:
                return R.drawable.ic_repeat_one;
            case 2:
                return R.drawable.ic_repeat_on;
            default:
                throw new IllegalArgumentException("Repeat mode not found!");
        }
    }

}