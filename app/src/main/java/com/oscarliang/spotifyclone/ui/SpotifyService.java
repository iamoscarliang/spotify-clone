package com.oscarliang.spotifyclone.ui;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class SpotifyService extends MediaSessionService {

    private MediaSession mMediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mMediaSession = new MediaSession.Builder(this, player).build();
    }

    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        mMediaSession.getPlayer().release();
        mMediaSession.release();
        mMediaSession = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mMediaSession;
    }

}
