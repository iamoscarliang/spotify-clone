package com.oscarliang.spotifyclone.ui.playlistedit;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.usecase.music.GetMusicsByIdsUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.UpdatePlaylistUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class PlaylistEditViewModel extends ViewModel {

    private final LiveData<Resource<List<Music>>> mMusics;
    private final LiveData<Event<Resource<Playlist>>> mUpdatePlaylistState;

    @VisibleForTesting
    final MutableLiveData<List<String>> mMusicIds = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<UpdatePlaylistQuery> mUpdatePlaylistQuery = new MutableLiveData<>();

    @Inject
    public PlaylistEditViewModel(GetMusicsByIdsUseCase getMusicsByIdsUseCase,
                                 UpdatePlaylistUseCase updatePlaylistUseCase) {
        mMusics = Transformations.switchMap(mMusicIds, titles -> {
            if (titles == null || titles.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getMusicsByIdsUseCase.execute(titles);
            }
        });
        mUpdatePlaylistState = Transformations.switchMap(mUpdatePlaylistQuery, query -> {
            if (query == null || query.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return updatePlaylistUseCase.execute(query.mUserId, query.mPlaylist, query.mPlaylistName, query.mMusics);
            }
        });
    }

    public LiveData<Resource<List<Music>>> getPlaylistMusics() {
        return mMusics;
    }

    public LiveData<Event<Resource<Playlist>>> getUpdatePlaylistState() {
        return mUpdatePlaylistState;
    }

    public void setPlaylistMusics(List<String> musicIds) {
        if (Objects.equals(mMusicIds.getValue(), musicIds)) {
            return;
        }
        mMusicIds.setValue(musicIds);
    }

    public void updatePlaylist(String userId, Playlist playlist, String playlistName, List<Music> musics) {
        mUpdatePlaylistQuery.setValue(new UpdatePlaylistQuery(userId, playlist, playlistName, musics));
    }

    public void retry() {
        List<String> current = mMusicIds.getValue();
        if (current != null && !current.isEmpty()) {
            mMusicIds.setValue(current);
        }
    }

    @VisibleForTesting
    public static class UpdatePlaylistQuery {

        private final String mUserId;
        private final Playlist mPlaylist;
        private final String mPlaylistName;
        private final List<Music> mMusics;

        public UpdatePlaylistQuery(String userId, Playlist playlist,
                                   String playlistName, List<Music> musics) {
            mUserId = userId;
            mPlaylist = playlist;
            mPlaylistName = playlistName;
            mMusics = musics;
        }

        public boolean isEmpty() {
            return mUserId == null || mPlaylist == null
                    || mPlaylistName == null || mMusics == null
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
            UpdatePlaylistQuery that = (UpdatePlaylistQuery) o;
            return Objects.equals(mUserId, that.mUserId)
                    && Objects.equals(mPlaylist, that.mPlaylist)
                    && Objects.equals(mPlaylistName, that.mPlaylistName)
                    && Objects.equals(mMusics, that.mMusics);
        }

    }

}
