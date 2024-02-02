package com.oscarliang.spotifyclone.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.usecase.playlist.AddMusicToNewPlaylistUseCase;
import com.oscarliang.spotifyclone.domain.usecase.playlist.AddMusicToPlaylistUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    private MediaController mController;

    private final LiveData<Event<Resource<Playlist>>> mAddToPlaylistState;
    private final LiveData<Event<Resource<Playlist>>> mAddToNewPlaylistState;

    private final MutableLiveData<AddToPlaylistQuery> mAddToPlaylistQuery = new MutableLiveData<>();
    private final MutableLiveData<AddToNewPlaylistQuery> mAddToNewPlaylistQuery = new MutableLiveData<>();

    private final MutableLiveData<List<Music>> mMusics = new MutableLiveData<>();
    private final MutableLiveData<Music> mCurrentMusic = new MutableLiveData<>();
    private final MutableLiveData<Long> mDuration = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>();

    @Inject
    public MainViewModel(ListenableFuture<MediaController> mediaController,
                         AddMusicToPlaylistUseCase addMusicToPlaylistUseCase,
                         AddMusicToNewPlaylistUseCase addMusicToNewPlaylistUseCase) {
        mediaController.addListener(() -> {
            try {
                mController = mediaController.get();
                mController.addListener(new Player.Listener() {
                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        if (mediaItem != null && mediaItem.mediaMetadata != null) {
                            // Update current music when transition
                            Music music = mediaItem.mediaMetadata.extras.getParcelable("music");
                            mCurrentMusic.postValue(music);
                        } else {
                            // Update when music end
                            mCurrentMusic.postValue(null);
                        }
                    }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        // Update playback state
                        mIsPlaying.postValue(isPlaying);
                    }

                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        // Update music duration when ready
                        if (playbackState == Player.STATE_READY) {
                            mDuration.postValue(mController.getDuration());
                        }
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
        mAddToPlaylistState = Transformations.switchMap(mAddToPlaylistQuery, query -> {
            if (query == null || query.mUserId == null || query.mPlaylist == null || query.mMusic == null) {
                return AbsentLiveData.create();
            } else {
                return addMusicToPlaylistUseCase.execute(query.mUserId, query.mPlaylist, query.mMusic);
            }
        });
        mAddToNewPlaylistState = Transformations.switchMap(mAddToNewPlaylistQuery, query -> {
            if (query == null || query.mUserId == null || query.mPlaylist == null || query.mMusic == null) {
                return AbsentLiveData.create();
            } else {
                return addMusicToNewPlaylistUseCase.execute(query.mUserId, query.mPlaylist, query.mMusic);
            }
        });
    }

    public LiveData<Event<Resource<Playlist>>> getAddToPlaylistState() {
        return mAddToPlaylistState;
    }

    public LiveData<Event<Resource<Playlist>>> getAddToNewPlaylistState() {
        return mAddToNewPlaylistState;
    }

    public void addToPlaylist(String userId, Playlist playlist, Music music) {
        mAddToPlaylistQuery.setValue(new AddToPlaylistQuery(userId, playlist, music));
    }

    public void addToNewPlaylist(String userId, String playlist, Music music) {
        mAddToNewPlaylistQuery.setValue(new AddToNewPlaylistQuery(userId, playlist, music));
    }

    public LiveData<List<Music>> getMusics() {
        return mMusics;
    }

    public LiveData<Music> getCurrentMusic() {
        return mCurrentMusic;
    }

    public LiveData<Long> getDuration() {
        return mDuration;
    }

    public LiveData<Boolean> getPlaying() {
        return mIsPlaying;
    }

    public boolean getShuffleModeEnabled() {
        return mController.getShuffleModeEnabled();
    }

    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        mController.setShuffleModeEnabled(shuffleModeEnabled);
    }

    public int getRepeatMode() {
        return mController.getRepeatMode();
    }

    public void setRepeatMode(int repeatMode) {
        mController.setRepeatMode(repeatMode);
    }

    public MediaMetadata getPlaylistMetadata() {
        return mController.getPlaylistMetadata();
    }

    public int getCurrentIndex() {
        return mController.getCurrentMediaItemIndex();
    }

    public long getCurrentTime() {
        return mController.getCurrentPosition();
    }

    public void skipNextMusic() {
        mController.seekToNextMediaItem();
    }

    public void skipPreviousMusic() {
        mController.seekToPreviousMediaItem();
    }

    public void seekTo(long position) {
        mController.seekTo(position);
    }

    public void toggleMusic() {
        if (mController.isPlaying()) {
            mController.pause();
        } else {
            mController.play();
        }
    }

    public void addMusic(Music music) {
        // Clear previous music and add new music to playlist
        mController.clearMediaItems();
        MediaItem mediaItem = createMediaItem(music);
        mController.addMediaItem(mediaItem);
        mController.prepare();
        // Update playlist music
        mMusics.setValue(Collections.singletonList(music));
    }

    public void addPlaylist(List<Music> musics, String playlistId) {
        // Clear previous music and add new music to playlist
        mController.clearMediaItems();
        for (Music music : musics) {
            MediaItem mediaItem = createMediaItem(music);
            mController.addMediaItem(mediaItem);
        }
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle(playlistId)
                .build();
        mController.setPlaylistMetadata(metadata);
        mController.prepare();
        // Update playlist music
        mMusics.setValue(musics);
    }

    private MediaItem createMediaItem(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle(music.getTitle())
                .setArtist(music.getArtist())
                .setAlbumArtist(music.getArtist())
                .setArtworkUri(Uri.parse(music.getImageUrl()))
                .setExtras(bundle)
                .build();

        return new MediaItem.Builder()
                .setUri(music.getMusicUrl())
                .setMediaMetadata(metadata)
                .build();
    }

    private static class AddToPlaylistQuery {

        private final String mUserId;
        private final Playlist mPlaylist;
        private final Music mMusic;

        public AddToPlaylistQuery(String userId, Playlist playlist, Music music) {
            mUserId = userId;
            mPlaylist = playlist;
            mMusic = music;
        }

    }

    private static class AddToNewPlaylistQuery {

        private final String mUserId;
        private final String mPlaylist;
        private final Music mMusic;

        public AddToNewPlaylistQuery(String userId, String playlist, Music music) {
            mUserId = userId;
            mPlaylist = playlist;
            mMusic = music;
        }

    }

}
