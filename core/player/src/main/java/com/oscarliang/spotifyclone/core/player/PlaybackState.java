package com.oscarliang.spotifyclone.core.player;

import androidx.annotation.NonNull;

import com.oscarliang.spotifyclone.core.model.Music;

import java.util.List;
import java.util.Objects;

public class PlaybackState {

    public final String musicId;
    public final long duration;
    public final int repeatMode;
    public final boolean isShuffleModeEnabled;
    public final boolean isPlaying;
    public final List<Music> musics;

    public PlaybackState(
            String musicId,
            long duration,
            int repeatMode,
            boolean isShuffleModeEnabled,
            boolean isPlaying,
            List<Music> musics
    ) {
        this.musicId = musicId;
        this.duration = duration;
        this.repeatMode = repeatMode;
        this.isShuffleModeEnabled = isShuffleModeEnabled;
        this.isPlaying = isPlaying;
        this.musics = musics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaybackState playbackState = (PlaybackState) o;
        return duration == playbackState.duration
                && repeatMode == playbackState.repeatMode
                && isShuffleModeEnabled == playbackState.isShuffleModeEnabled
                && isPlaying == playbackState.isPlaying
                && Objects.equals(musicId, playbackState.musicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicId, duration, repeatMode, isShuffleModeEnabled, isPlaying);
    }

    @NonNull
    @Override
    public String toString() {
        return "PlaybackState{" +
                "musicId='" + musicId + '\'' +
                ", duration=" + duration +
                ", repeatMode=" + repeatMode +
                ", isShuffleModeEnabled=" + isShuffleModeEnabled +
                ", isPlaying=" + isPlaying +
                '}';
    }

}