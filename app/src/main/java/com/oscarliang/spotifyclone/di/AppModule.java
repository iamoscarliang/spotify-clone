package com.oscarliang.spotifyclone.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oscarliang.spotifyclone.data.repository.AlbumDataRepository;
import com.oscarliang.spotifyclone.data.repository.ArtistDataRepository;
import com.oscarliang.spotifyclone.data.repository.CategoryDataRepository;
import com.oscarliang.spotifyclone.data.repository.MusicDataRepository;
import com.oscarliang.spotifyclone.data.repository.PlaylistDataRepository;
import com.oscarliang.spotifyclone.data.repository.UserDataRepository;
import com.oscarliang.spotifyclone.domain.repository.AlbumRepository;
import com.oscarliang.spotifyclone.domain.repository.ArtistRepository;
import com.oscarliang.spotifyclone.domain.repository.CategoryRepository;
import com.oscarliang.spotifyclone.domain.repository.MusicRepository;
import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.domain.repository.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class AppModule {

    @Singleton
    @Provides
    public FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Singleton
    @Provides
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    public AlbumRepository provideAlbumRepository(FirebaseFirestore db) {
        return new AlbumDataRepository(db);
    }

    @Singleton
    @Provides
    public ArtistRepository provideArtistRepository(FirebaseFirestore db) {
        return new ArtistDataRepository(db);
    }

    @Singleton
    @Provides
    public CategoryRepository provideCategoryRepository(FirebaseFirestore db) {
        return new CategoryDataRepository(db);
    }

    @Singleton
    @Provides
    public MusicRepository provideMusicRepository(FirebaseFirestore db) {
        return new MusicDataRepository(db);
    }

    @Singleton
    @Provides
    public PlaylistRepository providePlaylistRepository(FirebaseFirestore db) {
        return new PlaylistDataRepository(db);
    }

    @Singleton
    @Provides
    public UserRepository provideUserRepository(FirebaseAuth auth) {
        return new UserDataRepository(auth);
    }

}
