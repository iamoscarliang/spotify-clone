package com.oscarliang.spotifyclone.feature.playlistselect;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.domain.AddMusicToPlaylistUseCase;
import com.oscarliang.spotifyclone.core.model.Playlist;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class PlaylistSelectViewModel extends ViewModel {

    private final PlaylistRepository playlistRepository;
    private final AddMusicToPlaylistUseCase addMusicToPlaylistUseCase;
    private final Observable<Result<List<Playlist>>> result;

    @VisibleForTesting
    final BehaviorSubject<String> userId = BehaviorSubject.create();

    @Inject
    public PlaylistSelectViewModel(
            PlaylistRepository playlistRepository,
            AddMusicToPlaylistUseCase addMusicToPlaylistUseCase
    ) {
        this.playlistRepository = playlistRepository;
        this.addMusicToPlaylistUseCase = addMusicToPlaylistUseCase;
        this.result = userId.switchMap(id -> {
                    if (id == null || id.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return playlistRepository
                                .getPlaylistsByUserId(id)
                                .map(data -> Result.success(data))
                                .onErrorReturn(throwable -> Result.error(throwable.getMessage(), null))
                                .startWithItem(Result.loading());
                    }
                })
                .replay(1)
                .autoConnect();
    }

    public Observable<Result<List<Playlist>>> getResult() {
        return result;
    }

    public void setUserId(@NonNull String id) {
        if (Objects.equals(userId.getValue(), id)) {
            return;
        }
        userId.onNext(id);
    }

    public void retry() {
        String current = userId.getValue();
        if (current != null && !current.isEmpty()) {
            userId.onNext(current);
        }
    }

    public Completable createPlaylist(String name) {
        return playlistRepository.createPlaylist(name, userId.getValue());
    }

    public Completable addMusicToPlaylist(Playlist playlist, String musicId) {
        return addMusicToPlaylistUseCase.execute(playlist, musicId);
    }

}