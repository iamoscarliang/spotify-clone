package com.oscarliang.spotifyclone.core.player;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.model.Music;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@Singleton
public class ExoMusicPlayer implements MusicPlayer {

    private MediaController mediaController;

    private final BehaviorSubject<String> musicId = BehaviorSubject.createDefault(EMPTY_MUSIC);
    private final BehaviorSubject<Long> duration = BehaviorSubject.createDefault(0L);
    private final BehaviorSubject<Integer> repeatMode = BehaviorSubject.createDefault(Player.REPEAT_MODE_OFF);
    private final BehaviorSubject<Boolean> isShuffleModeEnabled = BehaviorSubject.createDefault(false);
    private final BehaviorSubject<Boolean> isPlaying = BehaviorSubject.createDefault(false);
    private final BehaviorSubject<List<Music>> musics = BehaviorSubject.createDefault(emptyList());

    private final Observable<PlaybackState> playbackState = Observable.combineLatest(
            musicId, duration, repeatMode, isShuffleModeEnabled, isPlaying, musics,
            (musicId, duration, repeatMode, isShuffleModeEnabled, isPlaying, musics) ->
                    new PlaybackState(musicId, duration, repeatMode, isShuffleModeEnabled, isPlaying, musics)
    );

    @Inject
    public ExoMusicPlayer(
            ListenableFuture<MediaController> future,
            AnalyticsLogger analyticsLogger
    ) {
        future.addListener(() -> {
            try {
                this.mediaController = future.get();
                this.mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        if (mediaItem != null
                                && mediaItem.mediaId != null
                                && mediaItem.mediaMetadata.title != null) {
                            musicId.onNext(mediaItem.mediaId);

                            // Log music play event
                            analyticsLogger.logEvent(
                                    AnalyticsEvent.MUSIC_PLAY,
                                    asList(
                                            new AnalyticsParam(AnalyticsParam.MUSIC_ID, mediaItem.mediaId),
                                            new AnalyticsParam(AnalyticsParam.MUSIC_TITLE, mediaItem.mediaMetadata.title.toString())
                                    )
                            );
                        } else {
                            // Update when music end
                            musicId.onNext(EMPTY_MUSIC);
                        }
                    }

                    @Override
                    public void onIsPlayingChanged(boolean playing) {
                        isPlaying.onNext(playing);
                    }

                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_READY) {
                            duration.onNext(mediaController.getDuration());
                        }
                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                        isShuffleModeEnabled.onNext(shuffleModeEnabled);
                    }

                    @Override
                    public void onRepeatModeChanged(int mode) {
                        repeatMode.onNext(mode);
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }

    @Override
    public Observable<PlaybackState> getPlaybackState() {
        return playbackState;
    }

    @Override
    public int getCurrentIndex() {
        return mediaController.getCurrentMediaItemIndex();
    }

    @Override
    public long getCurrentTime() {
        return mediaController.getCurrentPosition();
    }

    @Override
    public String getCurrentPlaylistId() {
        MediaMetadata metadata = mediaController.getPlaylistMetadata();
        if (metadata != null && metadata.title != null) {
            return metadata.title.toString();
        }
        return EMPTY_PLAYLIST;
    }

    @Override
    public void toggleMusic() {
        if (mediaController.isPlaying()) {
            mediaController.pause();
        } else {
            mediaController.play();
        }
    }

    @Override
    public void toggleShuffleModeEnabled() {
        boolean current = mediaController.getShuffleModeEnabled();
        mediaController.setShuffleModeEnabled(!current);
    }

    @Override
    public void toggleRepeatMode() {
        int updated = mediaController.getRepeatMode() + 1;
        if (updated > Player.REPEAT_MODE_ALL) {
            updated = Player.REPEAT_MODE_OFF;
        }
        mediaController.setRepeatMode(updated);
    }

    @Override
    public void skipNextMusic() {
        mediaController.seekToNextMediaItem();
    }

    @Override
    public void skipPreviousMusic() {
        mediaController.seekToPreviousMediaItem();
    }

    @Override
    public void seekTo(long position) {
        mediaController.seekTo(position);
    }

    @Override
    public void addMusic(@NonNull Music music) {
        // Clear previous music and add new music to playlist
        mediaController.clearMediaItems();
        MediaItem mediaItem = createMediaItem(music);
        mediaController.addMediaItem(mediaItem);
        mediaController.prepare();
        // Update playlist music
        musics.onNext(singletonList(music));
    }

    @Override
    public void addPlaylist(@NonNull List<Music> musics, @NonNull String playlistId) {
        if (musics.isEmpty()) {
            return;
        }
        // Clear previous music and add new music to playlist
        mediaController.clearMediaItems();
        for (Music music : musics) {
            MediaItem mediaItem = createMediaItem(music);
            mediaController.addMediaItem(mediaItem);
        }
        mediaController.setPlaylistMetadata(
                new MediaMetadata.Builder()
                        .setTitle(playlistId)
                        .build()
        );
        mediaController.prepare();
        // Update playlist music
        this.musics.onNext(musics);
    }

    @Override
    public void clearMusic() {
        // Stop the current music and clear all the media items.
        // Note that we do not call release(), or the player
        // will be unavailable when later the user re-login.
        mediaController.stop();
        mediaController.clearMediaItems();
    }

    private MediaItem createMediaItem(Music music) {
        return new MediaItem.Builder()
                .setMediaId(music.getId())
                .setUri(music.getMusicUrl())
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle(music.getTitle())
                                .setArtist(music.getArtist())
                                .setAlbumArtist(music.getArtist())
                                .setArtworkUri(Uri.parse(music.getImageUrl()))
                                .build()
                )
                .build();
    }

}