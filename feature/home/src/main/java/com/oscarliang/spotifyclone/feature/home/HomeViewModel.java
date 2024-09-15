package com.oscarliang.spotifyclone.feature.home;

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

public class HomeViewModel extends ViewModel {

    private final Observable<Result<Pair<List<Album>, List<Artist>>>> result;

    @VisibleForTesting
    final BehaviorSubject<HomeQuery> query = BehaviorSubject.create();

    @Inject
    public HomeViewModel(
            AlbumRepository albumRepository,
            ArtistRepository artistRepository
    ) {
        this.result = query.switchMap(query -> {
                    if (query == null || query.isEmpty()) {
                        return Observable.empty();
                    } else {
                        return Single.zip(
                                        albumRepository.getAllAlbums(query.albumsCount),
                                        artistRepository.getAllArtists(query.artistsCount),
                                        (allAlbums, allArtists) ->
                                                Pair.create(allAlbums, allArtists)
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

    public Observable<Result<Pair<List<Album>, List<Artist>>>> getResult() {
        return result;
    }

    public void setQuery(@NonNull HomeQuery query) {
        if (Objects.equals(this.query.getValue(), query)) {
            return;
        }
        this.query.onNext(query);
    }

    public void retry() {
        HomeQuery current = query.getValue();
        if (current != null && !current.isEmpty()) {
            query.onNext(current);
        }
    }

}