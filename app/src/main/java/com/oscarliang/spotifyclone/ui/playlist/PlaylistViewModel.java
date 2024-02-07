package com.oscarliang.spotifyclone.ui.playlist;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.usecase.music.GetMusicsByIdsUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.DeletePlaylistUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class PlaylistViewModel extends ViewModel {

    private final LiveData<Resource<List<Music>>> mMusics;
    private final LiveData<Event<Resource<Playlist>>> mDeletePlaylistState;

    @VisibleForTesting
    final MutableLiveData<List<String>> mMusicIds = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<DeletePlaylistQuery> mDeletePlaylistQuery = new MutableLiveData<>();

    @Inject
    public PlaylistViewModel(GetMusicsByIdsUseCase getMusicsByIdsUseCase,
                             DeletePlaylistUseCase deletePlaylistUseCase) {
        mMusics = Transformations.switchMap(mMusicIds, ids -> {
            if (ids == null || ids.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getMusicsByIdsUseCase.execute(ids);
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

    public LiveData<Resource<List<Music>>> getPlaylistMusics() {
        return mMusics;
    }

    public LiveData<Event<Resource<Playlist>>> getDeletePlaylistState() {
        return mDeletePlaylistState;
    }

    public void setPlaylistMusics(List<String> musicIds) {
        if (Objects.equals(mMusicIds.getValue(), musicIds)) {
            return;
        }
        mMusicIds.setValue(musicIds);
    }

    public void deletePlaylist(String userId, Playlist playlist) {
        mDeletePlaylistQuery.setValue(new DeletePlaylistQuery(userId, playlist));
    }

    public void retry() {
        List<String> current = mMusicIds.getValue();
        if (current != null && !current.isEmpty()) {
            mMusicIds.setValue(current);
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
            return mUserId == null || mPlaylist == null || mUserId.isEmpty();
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
