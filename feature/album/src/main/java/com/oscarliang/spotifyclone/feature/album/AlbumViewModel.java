package com.oscarliang.spotifyclone.feature.album;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.AlbumRepository;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.model.Music;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class AlbumViewModel extends ViewModel {

    private final Observable<Result<Pair<Album, List<Music>>>> result;

    @VisibleForTesting
    final BehaviorSubject<String> albumId = BehaviorSubject.create();

    @Inject
    public AlbumViewModel(
            AlbumRepository albumRepository,
            MusicRepository musicRepository
    ) {
        this.result = albumId.switchMap(id -> {
                    if (id == null || id.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return Single.zip(
                                        albumRepository.getAlbumById(id),
                                        musicRepository.getMusicsByAlbumId(id),
                                        (album, musics) -> Pair.create(album, musics)
                                )
                                .map(data -> Result.success(data))
                                .onErrorReturn(throwable -> Result.error(throwable.getMessage(), null))
                                .toObservable()
                                .startWithItem(Result.loading());
                    }
                })
                .replay(1)
                .autoConnect();
    }

    public Observable<Result<Pair<Album, List<Music>>>> getResult() {
        return result;
    }

    public void setAlbumId(@NonNull String id) {
        if (Objects.equals(albumId.getValue(), id)) {
            return;
        }
        albumId.onNext(id);
    }

    public void retry() {
        String current = albumId.getValue();
        if (current != null && !current.isEmpty()) {
            albumId.onNext(current);
        }
    }

}