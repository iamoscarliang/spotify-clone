package com.oscarliang.spotifyclone.core.network.di;

import com.oscarliang.spotifyclone.core.network.api.AlbumService;
import com.oscarliang.spotifyclone.core.network.api.ArtistService;
import com.oscarliang.spotifyclone.core.network.api.CategoryService;
import com.oscarliang.spotifyclone.core.network.api.FirebaseAlbumService;
import com.oscarliang.spotifyclone.core.network.api.FirebaseArtistService;
import com.oscarliang.spotifyclone.core.network.api.FirebaseCategoryService;
import com.oscarliang.spotifyclone.core.network.api.FirebaseMusicService;
import com.oscarliang.spotifyclone.core.network.api.FirebasePlaylistService;
import com.oscarliang.spotifyclone.core.network.api.MusicService;
import com.oscarliang.spotifyclone.core.network.api.PlaylistService;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = FirebaseModule.class)
public abstract class NetworkModule {

    @Singleton
    @Binds
    public abstract ArtistService bindArtistService(
            FirebaseArtistService artistService
    );

    @Singleton
    @Binds
    public abstract AlbumService bindAlbumService(
            FirebaseAlbumService albumService
    );

    @Singleton
    @Binds
    public abstract CategoryService bindCategoryService(
            FirebaseCategoryService categoryService
    );

    @Singleton
    @Binds
    public abstract MusicService bindMusicService(
            FirebaseMusicService musicService
    );

    @Singleton
    @Binds
    public abstract PlaylistService bindPlaylistService(
            FirebasePlaylistService playlistService
    );

}