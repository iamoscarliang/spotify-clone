package com.oscarliang.spotifyclone.di;

import com.oscarliang.spotifyclone.ui.album.AlbumFragment;
import com.oscarliang.spotifyclone.ui.artist.ArtistFragment;
import com.oscarliang.spotifyclone.ui.category.CategoryFragment;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.AddToPlaylistBottomSheet;
import com.oscarliang.spotifyclone.ui.home.HomeFragment;
import com.oscarliang.spotifyclone.ui.library.LibraryFragment;
import com.oscarliang.spotifyclone.ui.login.LoginFragment;
import com.oscarliang.spotifyclone.ui.music.MusicFragment;
import com.oscarliang.spotifyclone.ui.playlist.PlaylistFragment;
import com.oscarliang.spotifyclone.ui.playlistedit.PlaylistEditFragment;
import com.oscarliang.spotifyclone.ui.search.SearchFragment;
import com.oscarliang.spotifyclone.ui.searchresult.album.AlbumSearchResultFragment;
import com.oscarliang.spotifyclone.ui.searchresult.artist.ArtistSearchResultFragment;
import com.oscarliang.spotifyclone.ui.searchresult.music.MusicSearchResultFragment;
import com.oscarliang.spotifyclone.ui.searchresult.SearchResultFragment;
import com.oscarliang.spotifyclone.ui.signup.SignupFragment;
import com.oscarliang.spotifyclone.ui.signupname.SignupNameFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    //--------------------------------------------------------
    // Methods
    //--------------------------------------------------------
    @ContributesAndroidInjector
    abstract SignupFragment contributeSignupFragment();

    @ContributesAndroidInjector
    abstract SignupNameFragment contributeSignupNameFragment();

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract AlbumFragment contributeAlbumFragment();

    @ContributesAndroidInjector
    abstract ArtistFragment contributeArtistFragment();

    @ContributesAndroidInjector
    abstract MusicFragment contributeMusicFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();

    @ContributesAndroidInjector
    abstract CategoryFragment contributeCategoryFragment();

    @ContributesAndroidInjector
    abstract SearchResultFragment contributeSearchResultFragment();

    @ContributesAndroidInjector
    abstract MusicSearchResultFragment contributeMusicSearchResultFragment();

    @ContributesAndroidInjector
    abstract ArtistSearchResultFragment contributeArtistSearchResultFragment();

    @ContributesAndroidInjector
    abstract AlbumSearchResultFragment contributeAlbumSearchResultFragment();

    @ContributesAndroidInjector
    abstract LibraryFragment contributeLibraryFragment();

    @ContributesAndroidInjector
    abstract PlaylistFragment contributePlaylistFragment();

    @ContributesAndroidInjector
    abstract PlaylistEditFragment contributePlaylistEditFragment();

    @ContributesAndroidInjector
    abstract AddToPlaylistBottomSheet contributeAddToPlaylistBottomSheet();
    //========================================================

}
