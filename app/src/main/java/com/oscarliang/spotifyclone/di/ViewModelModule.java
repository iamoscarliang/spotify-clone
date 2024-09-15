package com.oscarliang.spotifyclone.di;

import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.feature.album.AlbumViewModel;
import com.oscarliang.spotifyclone.feature.artist.ArtistViewModel;
import com.oscarliang.spotifyclone.feature.category.CategoryViewModel;
import com.oscarliang.spotifyclone.feature.home.HomeViewModel;
import com.oscarliang.spotifyclone.feature.library.LibraryViewModel;
import com.oscarliang.spotifyclone.feature.login.LoginViewModel;
import com.oscarliang.spotifyclone.feature.musicinfo.MusicInfoViewModel;
import com.oscarliang.spotifyclone.feature.player.PlayerViewModel;
import com.oscarliang.spotifyclone.feature.playlist.PlaylistViewModel;
import com.oscarliang.spotifyclone.feature.playlistedit.PlaylistEditViewModel;
import com.oscarliang.spotifyclone.feature.playlistinfo.PlaylistInfoViewModel;
import com.oscarliang.spotifyclone.feature.playlistselect.PlaylistSelectViewModel;
import com.oscarliang.spotifyclone.feature.search.SearchViewModel;
import com.oscarliang.spotifyclone.feature.searchresult.AlbumResultViewModel;
import com.oscarliang.spotifyclone.feature.searchresult.ArtistResultViewModel;
import com.oscarliang.spotifyclone.feature.searchresult.MusicResultViewModel;
import com.oscarliang.spotifyclone.feature.searchresult.SearchResultViewModel;
import com.oscarliang.spotifyclone.feature.signup.SignupViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindLoginViewModel(LoginViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel.class)
    public abstract ViewModel bindSignupViewModel(SignupViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    public abstract ViewModel bindHomeViewModel(HomeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    public abstract ViewModel bindSearchViewModel(SearchViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LibraryViewModel.class)
    public abstract ViewModel bindLibraryViewModel(LibraryViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AlbumViewModel.class)
    public abstract ViewModel bindAlbumViewModel(AlbumViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ArtistViewModel.class)
    public abstract ViewModel bindArtistViewModel(ArtistViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel.class)
    public abstract ViewModel bindCategoryViewModel(CategoryViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchResultViewModel.class)
    public abstract ViewModel bindSearchResultViewModel(SearchResultViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MusicResultViewModel.class)
    public abstract ViewModel bindMusicSearchResultViewModel(MusicResultViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AlbumResultViewModel.class)
    public abstract ViewModel bindAlbumSearchResultViewModel(AlbumResultViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ArtistResultViewModel.class)
    public abstract ViewModel bindArtistSearchResultViewModel(ArtistResultViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistViewModel.class)
    public abstract ViewModel bindPlaylistViewModel(PlaylistViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistEditViewModel.class)
    public abstract ViewModel bindPlaylistEditViewModel(PlaylistEditViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistSelectViewModel.class)
    public abstract ViewModel bindPlaylistSelectViewModel(PlaylistSelectViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistInfoViewModel.class)
    public abstract ViewModel bindPlaylistInfoViewModel(PlaylistInfoViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MusicInfoViewModel.class)
    public abstract ViewModel bindMusicInfoViewModel(MusicInfoViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel.class)
    public abstract ViewModel bindPlayerViewModel(PlayerViewModel viewModel);

}