package com.oscarliang.spotifyclone.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.usecase.playlist.AddNewPlaylistUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.DeletePlaylistUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.GetPlaylistsByUserIdUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class LibraryViewModel extends ViewModel {

    private final LiveData<Resource<List<Playlist>>> mPlaylists;
    private final LiveData<Event<Resource<Playlist>>> mAddPlaylistState;
    private final LiveData<Event<Resource<Playlist>>> mDeletePlaylistState;

    private final MutableLiveData<String> mUser = new MutableLiveData<>();
    private final MutableLiveData<AddNewPlaylistQuery> mAddNewPlaylistQuery = new MutableLiveData<>();
    private final MutableLiveData<DeletePlaylistQuery> mPlaylistToDelete = new MutableLiveData<>();

    @Inject
    public LibraryViewModel(GetPlaylistsByUserIdUseCase getPlaylistsByUserIdUseCase,
                            AddNewPlaylistUseCase addNewPlaylistUseCase,
                            DeletePlaylistUseCase deletePlaylistUseCase) {
        mPlaylists = Transformations.switchMap(mUser, user -> {
            if (user == null || user.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getPlaylistsByUserIdUseCase.execute(user);
            }
        });
        mAddPlaylistState = Transformations.switchMap(mAddNewPlaylistQuery, query -> {
            if (query == null || query.mUserId == null || query.mPlaylistName == null) {
                return AbsentLiveData.create();
            } else {
                return addNewPlaylistUseCase.execute(query.mUserId, query.mPlaylistName);
            }
        });
        mDeletePlaylistState = Transformations.switchMap(mPlaylistToDelete, query -> {
            if (query == null || query.mUserId == null || query.mPlaylist == null) {
                return AbsentLiveData.create();
            } else {
                return deletePlaylistUseCase.execute(query.mUserId, query.mPlaylist);
            }
        });
    }

    public LiveData<Resource<List<Playlist>>> getPlaylists() {
        return mPlaylists;
    }

    public LiveData<Event<Resource<Playlist>>> getAddPlaylistState() {
        return mAddPlaylistState;
    }

    public LiveData<Event<Resource<Playlist>>> getDeletePlaylistState() {
        return mDeletePlaylistState;
    }

    public void setUser(String user) {
        mUser.setValue(user);
    }

    public void addPlaylist(String userId, String playlistName) {
        mAddNewPlaylistQuery.setValue(new AddNewPlaylistQuery(userId, playlistName));
    }

    public void deletePlaylist(String userId, Playlist playlist) {
        mPlaylistToDelete.setValue(new DeletePlaylistQuery(userId, playlist));
    }

    private static class AddNewPlaylistQuery {

        private final String mUserId;
        private final String mPlaylistName;

        public AddNewPlaylistQuery(String userId, String playlist) {
            mUserId = userId;
            mPlaylistName = playlist;
        }

    }

    private static class DeletePlaylistQuery {

        private final String mUserId;
        private final Playlist mPlaylist;

        public DeletePlaylistQuery(String userId, Playlist playlist) {
            mUserId = userId;
            mPlaylist = playlist;
        }

    }

}
