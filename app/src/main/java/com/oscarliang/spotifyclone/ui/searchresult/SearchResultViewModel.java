package com.oscarliang.spotifyclone.ui.searchresult;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import javax.inject.Inject;

public class SearchResultViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<String> mQuery = new MutableLiveData<>();

    @Inject
    public SearchResultViewModel() {
    }

    public MutableLiveData<String> getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        if (Objects.equals(mQuery.getValue(), query)) {
            return;
        }
        mQuery.setValue(query);
    }

    public boolean isEmpty() {
        return mQuery.getValue() == null || mQuery.getValue().isEmpty();
    }

}
