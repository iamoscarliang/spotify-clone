package com.oscarliang.spotifyclone.di;

import com.oscarliang.spotifyclone.feature.album.AlbumFragment;
import com.oscarliang.spotifyclone.feature.artist.ArtistFragment;
import com.oscarliang.spotifyclone.feature.category.CategoryFragment;
import com.oscarliang.spotifyclone.feature.home.HomeFragment;
import com.oscarliang.spotifyclone.feature.library.LibraryFragment;
import com.oscarliang.spotifyclone.feature.login.LoginFragment;
import com.oscarliang.spotifyclone.feature.musicinfo.MusicInfoBottomSheet;
import com.oscarliang.spotifyclone.feature.player.MiniPlayerFragment;
import com.oscarliang.spotifyclone.feature.player.PlayerFragment;
import com.oscarliang.spotifyclone.feature.playlist.PlaylistFragment;
import com.oscarliang.spotifyclone.feature.playlistedit.PlaylistEditFragment;
import com.oscarliang.spotifyclone.feature.playlistinfo.PlaylistInfoBottomSheet;
import com.oscarliang.spotifyclone.feature.playlistselect.PlaylistSelectFragment;
import com.oscarliang.spotifyclone.feature.search.SearchFragment;
import com.oscarliang.spotifyclone.feature.searchresult.AlbumResultFragment;
import com.oscarliang.spotifyclone.feature.searchresult.ArtistResultFragment;
import com.oscarliang.spotifyclone.feature.searchresult.MusicResultFragment;
import com.oscarliang.spotifyclone.feature.searchresult.SearchResultFragment;
import com.oscarliang.spotifyclone.feature.signup.SignupFragment;
import com.oscarliang.spotifyclone.feature.welcome.WelcomeFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract WelcomeFragment contributeWelcomeFragment();

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    abstract SignupFragment contributeSignupFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();

    @ContributesAndroidInjector
    abstract LibraryFragment contributeLibraryFragment();

    @ContributesAndroidInjector
    abstract AlbumFragment contributeAlbumFragment();

    @ContributesAndroidInjector
    abstract ArtistFragment contributeArtistFragment();

    @ContributesAndroidInjector
    abstract CategoryFragment contributeCategoryFragment();

    @ContributesAndroidInjector
    abstract SearchResultFragment contributeSearchResultFragment();

    @ContributesAndroidInjector
    abstract MusicResultFragment contributeMusicSearchResultFragment();

    @ContributesAndroidInjector
    abstract AlbumResultFragment contributeAlbumSearchResultFragment();

    @ContributesAndroidInjector
    abstract ArtistResultFragment contributeArtistSearchResultFragment();

    @ContributesAndroidInjector
    abstract PlaylistFragment contributePlaylistFragment();

    @ContributesAndroidInjector
    abstract PlaylistEditFragment contributePlaylistEditFragment();

    @ContributesAndroidInjector
    abstract PlaylistSelectFragment contributePlaylistSelectFragment();

    @ContributesAndroidInjector
    abstract PlaylistInfoBottomSheet contributePlaylistInfoBottomSheet();

    @ContributesAndroidInjector
    abstract MusicInfoBottomSheet contributeMusicInfoBottomSheet();

    @ContributesAndroidInjector
    abstract PlayerFragment contributePlayerFragment();

    @ContributesAndroidInjector
    abstract MiniPlayerFragment contributeMiniPlayerFragment();

}