package com.oscarliang.spotifyclone.ui.playlistedit;

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

import javax.inject.Inject;

public class PlaylistEditViewModel extends ViewModel {

    private final LiveData<Resource<List<Music>>> mMusics;
    private final LiveData<Event<Resource<Playlist>>> mUpdatePlaylistState;

    private final MutableLiveData<List<String>> mMusicTitles = new MutableLiveData<>();
    private final MutableLiveData<UpdatePlaylistQuery> mUpdateQuery = new MutableLiveData<>();

    @Inject
    public PlaylistEditViewModel(GetMusicsByIdsUseCase getMusicsByIdsUseCase,
                                 UpdatePlaylistUseCase updatePlaylistUseCase) {
        mMusics = Transformations.switchMap(mMusicTitles, titles -> {
            if (titles == null || titles.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return getMusicsByIdsUseCase.execute(titles);
            }
        });
        mUpdatePlaylistState = Transformations.switchMap(mUpdateQuery, query -> {
            if (query == null
                    || query.mUserId == null || query.mPlaylist == null
                    || query.mPlaylistName == null || query.mMusics == null) {
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

    public void setPlaylistMusics(List<String> musicTitles) {
        mMusicTitles.setValue(musicTitles);
    }

    public void updatePlaylist(String userId, Playlist playlist, String playlistName, List<Music> musics) {
        mUpdateQuery.setValue(new UpdatePlaylistQuery(userId, playlist, playlistName, musics));
    }

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

    }

}
