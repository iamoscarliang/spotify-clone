package com.oscarliang.spotifyclone.ui.library;

import androidx.annotation.VisibleForTesting;
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
import java.util.Objects;

import javax.inject.Inject;

public class LibraryViewModel extends ViewModel {

    private final LiveData<Resource<List<Playlist>>> mPlaylists;
    private final LiveData<Event<Resource<Playlist>>> mAddPlaylistState;
    private final LiveData<Event<Resource<Playlist>>> mDeletePlaylistState;

    @VisibleForTesting
    final MutableLiveData<String> mUser = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<AddNewPlaylistQuery> mAddNewPlaylistQuery = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<DeletePlaylistQuery> mDeletePlaylistQuery = new MutableLiveData<>();

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
            if (query == null || query.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return addNewPlaylistUseCase.execute(query.mUserId, query.mPlaylistName);
            }
        });
        mDeletePlaylistState = Transformations.switchMap(mDeletePlaylistQuery, query -> {
            if (query == null || query.isEmpty()) {
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
        if (Objects.equals(mUser.getValue(), user)) {
            return;
        }
        mUser.setValue(user);
    }

    public void addPlaylist(String userId, String playlistName) {
        mAddNewPlaylistQuery.setValue(new AddNewPlaylistQuery(userId, playlistName));
    }

    public void deletePlaylist(String userId, Playlist playlist) {
        mDeletePlaylistQuery.setValue(new DeletePlaylistQuery(userId, playlist));
    }

    public void refresh() {
        if (mUser.getValue() != null) {
            mUser.setValue(mUser.getValue());
        }
    }

    @VisibleForTesting
    public static class AddNewPlaylistQuery {

        private final String mUserId;
        private final String mPlaylistName;

        public AddNewPlaylistQuery(String userId, String playlist) {
            mUserId = userId;
            mPlaylistName = playlist;
        }

        public boolean isEmpty() {
            return mUserId == null || mPlaylistName == null
                    || mUserId.isEmpty() || mPlaylistName.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AddNewPlaylistQuery that = (AddNewPlaylistQuery) o;
            return Objects.equals(mUserId, that.mUserId)
                    && Objects.equals(mPlaylistName, that.mPlaylistName);
        }

    }

    @VisibleForTesting
    public static class DeletePlaylistQuery {

        private final String mUserId;
        private final Playlist mPlaylist;

        public DeletePlaylistQuery(String userId, Playlist playlist) {
            mUserId = userId;
            mPlaylist = playlist;
        }

        public boolean isEmpty() {
            return mUserId == null || mUserId.isEmpty() || mPlaylist == null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DeletePlaylistQuery that = (DeletePlaylistQuery) o;
            return Objects.equals(mUserId, that.mUserId)
                    && Objects.equals(mPlaylist, that.mPlaylist);
        }

    }

}
