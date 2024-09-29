package com.oscarliang.spotifyclone.feature.playlistinfo;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.model.Playlist;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class PlaylistInfoViewModel extends ViewModel {

    private final Observable<Result<Playlist>> result;

    @VisibleForTesting
    final BehaviorSubject<String> playlistId = BehaviorSubject.create();

    @Inject
    public PlaylistInfoViewModel(PlaylistRepository playlistRepository) {
        this.result = playlistId.switchMap(id -> {
                    if (id == null || id.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return playlistRepository
                                .getCachedPlaylistById(id)
                                .map(data -> Result.success(data))
                                .toObservable()
                                .startWithItem(Result.loading());
                    }
                })
                .replay(1)
                .autoConnect();
    }

    public Observable<Result<Playlist>> getResult() {
        return result;
    }

    public void setPlaylistId(@NonNull String id) {
        if (Objects.equals(playlistId.getValue(), id)) {
            return;
        }
        playlistId.onNext(id);
    }

}