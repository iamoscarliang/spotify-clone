package com.oscarliang.spotifyclone.ui.playlist;

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

import javax.inject.Inject;

public class PlaylistViewModel extends ViewModel {

    private final LiveData<Resource<List<Music>>> mMusics;
    private final LiveData<Event<Resource<Playlist>>> mDeletePlaylistState;

    private final MutableLiveData<List<String>> mMusicIds = new MutableLiveData<>();
    private final MutableLiveData<DeletePlaylistQuery> mPlaylistToDelete = new MutableLiveData<>();

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
        mDeletePlaylistState = Transformations.switchMap(mPlaylistToDelete, query -> {
            if (query == null || query.mUserId == null || query.mPlaylist == null) {
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

    public void setPlaylistMusics(List<String> musicTitles) {
        mMusicIds.setValue(musicTitles);
    }

    public void deletePlaylist(String userId, Playlist playlist) {
        mPlaylistToDelete.setValue(new DeletePlaylistQuery(userId, playlist));
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
