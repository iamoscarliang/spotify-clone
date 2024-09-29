package com.oscarliang.spotifyclone.core.data.di;

import com.oscarliang.spotifyclone.core.data.repository.AlbumRepository;
import com.oscarliang.spotifyclone.core.data.repository.ArtistRepository;
import com.oscarliang.spotifyclone.core.data.repository.CategoryRepository;
import com.oscarliang.spotifyclone.core.data.repository.DefaultAlbumRepository;
import com.oscarliang.spotifyclone.core.data.repository.DefaultArtistRepository;
import com.oscarliang.spotifyclone.core.data.repository.DefaultCategoryRepository;
import com.oscarliang.spotifyclone.core.data.repository.DefaultMusicRepository;
import com.oscarliang.spotifyclone.core.data.repository.DefaultPlaylistRepository;
import com.oscarliang.spotifyclone.core.data.repository.DefaultRecentSearchRepository;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.data.repository.RecentSearchRepository;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class DataModule {

    @Binds
    public abstract ArtistRepository bindArtistRepository(
            DefaultArtistRepository artistRepository
    );

    @Binds
    public abstract AlbumRepository bindAlbumRepository(
            DefaultAlbumRepository albumRepository
    );

    @Binds
    public abstract CategoryRepository bindCategoryRepository(
            DefaultCategoryRepository categoryRepository
    );

    @Binds
    public abstract MusicRepository bindMusicRepository(
            DefaultMusicRepository musicRepository
    );

    @Binds
    public abstract PlaylistRepository bindPlaylistRepository(
            DefaultPlaylistRepository playlistRepository
    );

    @Binds
    public abstract RecentSearchRepository bindRecentSearchRepository(
            DefaultRecentSearchRepository recentSearchRepository
    );

}