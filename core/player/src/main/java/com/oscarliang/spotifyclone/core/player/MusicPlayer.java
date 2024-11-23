package com.oscarliang.spotifyclone.core.player;

import androidx.annotation.NonNull;

import com.oscarliang.spotifyclone.core.model.Music;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface MusicPlayer {

    String EMPTY_MUSIC = "empty_music";
    String EMPTY_PLAYLIST = "empty_playlist";

    Observable<PlaybackState> getPlaybackState();

    int getCurrentIndex();

    long getCurrentTime();

    String getCurrentPlaylistId();

    void toggleMusic();

    void toggleShuffleModeEnabled();

    void toggleRepeatMode();

    void skipNextMusic();

    void skipPreviousMusic();

    void seekTo(long position);

    void addMusic(@NonNull Music music);

    void addPlaylist(@NonNull List<Music> musics, @NonNull String playlistId);

    void clearMusic();

}