package com.oscarliang.spotifyclone.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.ui.ViewModelFactory;
import com.oscarliang.spotifyclone.ui.album.AlbumViewModel;
import com.oscarliang.spotifyclone.ui.artist.ArtistViewModel;
import com.oscarliang.spotifyclone.ui.category.CategoryViewModel;
import com.oscarliang.spotifyclone.ui.home.HomeViewModel;
import com.oscarliang.spotifyclone.ui.library.LibraryViewModel;
import com.oscarliang.spotifyclone.ui.login.LoginViewModel;
import com.oscarliang.spotifyclone.ui.playlist.PlaylistViewModel;
import com.oscarliang.spotifyclone.ui.playlistedit.PlaylistEditViewModel;
import com.oscarliang.spotifyclone.ui.search.SearchViewModel;
import com.oscarliang.spotifyclone.ui.searchresult.SearchResultViewModel;
import com.oscarliang.spotifyclone.ui.searchresult.album.AlbumSearchResultViewModel;
import com.oscarliang.spotifyclone.ui.searchresult.artist.ArtistSearchResultViewModel;
import com.oscarliang.spotifyclone.ui.searchresult.music.MusicSearchResultViewModel;
import com.oscarliang.spotifyclone.ui.signup.SignupViewModel;
import com.oscarliang.spotifyclone.ui.signupname.SignupNameViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    //--------------------------------------------------------
    // Methods
    //--------------------------------------------------------
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel.class)
    public abstract ViewModel bindSignupViewModel(SignupViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SignupNameViewModel.class)
    public abstract ViewModel bindSignupNameViewModel(SignupNameViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindLoginViewModel(LoginViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    public abstract ViewModel bindHomeViewModel(HomeViewModel viewModel);

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
    @ViewModelKey(SearchViewModel.class)
    public abstract ViewModel bindSearchViewModel(SearchViewModel viewModel);

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
    @ViewModelKey(MusicSearchResultViewModel.class)
    public abstract ViewModel bindMusicSearchResultViewModel(MusicSearchResultViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ArtistSearchResultViewModel.class)
    public abstract ViewModel bindArtistSearchResultViewModel(ArtistSearchResultViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AlbumSearchResultViewModel.class)
    public abstract ViewModel bindAlbumSearchResultViewModel(AlbumSearchResultViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LibraryViewModel.class)
    public abstract ViewModel bindLibraryViewModel(LibraryViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistViewModel.class)
    public abstract ViewModel bindPlaylistViewModel(PlaylistViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistEditViewModel.class)
    public abstract ViewModel bindPlaylistEditViewModel(PlaylistEditViewModel viewModel);

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);
    //========================================================

}
