package com.oscarliang.spotifyclone.core.player.di;

import com.oscarliang.spotifyclone.core.player.ExoMusicPlayer;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PlayerModule {

    @Singleton
    @Binds
    public abstract MusicPlayer bindMusicPlayer(
            ExoMusicPlayer musicPlayer
    );

}