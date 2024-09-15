package com.oscarliang.spotifyclone.feature.search;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.CategoryRepository;
import com.oscarliang.spotifyclone.core.model.Category;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class SearchViewModel extends ViewModel {

    private final Observable<Result<List<Category>>> result;

    private final BehaviorSubject<Query.Direction> direction = BehaviorSubject.createDefault(Query.Direction.ASCENDING);

    @Inject
    public SearchViewModel(CategoryRepository categoryRepository) {
        this.result = direction.switchMap(query -> {
                    if (query == null) {
                        return Observable.empty();
                    } else {
                        return categoryRepository
                                .getAllCategories(query)
                                .map(data -> Result.success(data))
                                .onErrorReturn(throwable -> Result.error(throwable.getMessage(), null))
                                .toObservable()
                                .startWithItem(Result.loading());
                    }
                })
                .replay(1)
                .autoConnect();
    }

    public Observable<Result<List<Category>>> getResult() {
        return result;
    }

    public Observable<Query.Direction> getDirection() {
        return direction;
    }

    public void retry() {
        Query.Direction current = direction.getValue();
        if (current != null) {
            direction.onNext(current);
        }
    }

    public void onToggleSort() {
        Query.Direction current = direction.getValue();
        if (current == Query.Direction.ASCENDING) {
            direction.onNext(Query.Direction.DESCENDING);
        } else {
            direction.onNext(Query.Direction.ASCENDING);
        }
    }

}