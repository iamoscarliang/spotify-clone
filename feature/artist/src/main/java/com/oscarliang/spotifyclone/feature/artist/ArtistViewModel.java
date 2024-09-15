package com.oscarliang.spotifyclone.feature.artist;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.AlbumRepository;
import com.oscarliang.spotifyclone.core.data.repository.ArtistRepository;
import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.model.Artist;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ArtistViewModel extends ViewModel {

    private final Observable<Result<Pair<Artist, List<Album>>>> result;

    @VisibleForTesting
    final BehaviorSubject<String> artistId = BehaviorSubject.create();

    @Inject
    public ArtistViewModel(
            ArtistRepository artistRepository,
            AlbumRepository albumRepository
    ) {
        this.result = artistId.switchMap(id -> {
                    if (id == null || id.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return Single.zip(
                                        artistRepository.getArtistById(id),
                                        albumRepository.getAlbumsByArtistId(id),
                                        (artist, albums) -> Pair.create(artist, albums)
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

    public Observable<Result<Pair<Artist, List<Album>>>> getResult() {
        return result;
    }

    public void setArtistId(@NonNull String id) {
        if (Objects.equals(artistId.getValue(), id)) {
            return;
        }
        artistId.onNext(id);
    }

    public void retry() {
        String current = artistId.getValue();
        if (current != null && !current.isEmpty()) {
            artistId.onNext(current);
        }
    }

}