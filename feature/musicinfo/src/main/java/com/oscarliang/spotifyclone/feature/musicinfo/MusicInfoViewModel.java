package com.oscarliang.spotifyclone.feature.musicinfo;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.model.Music;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class MusicInfoViewModel extends ViewModel {

    private final Observable<Result<Music>> result;

    @VisibleForTesting
    final BehaviorSubject<String> musicId = BehaviorSubject.create();

    @Inject
    public MusicInfoViewModel(MusicRepository musicRepository) {
        this.result = musicId.switchMap(id -> {
                    if (id == null || id.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return musicRepository
                                .getCachedMusicsById(id)
                                .map(data -> Result.success(data))
                                .toObservable()
                                .startWithItem(Result.loading());
                    }
                })
                .replay(1)
                .autoConnect();
    }

    public Observable<Result<Music>> getResult() {
        return result;
    }

    public void setMusicId(@NonNull String id) {
        if (Objects.equals(musicId.getValue(), id)) {
            return;
        }
        musicId.onNext(id);
    }

}