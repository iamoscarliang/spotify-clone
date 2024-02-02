package com.oscarliang.spotifyclone.ui.searchresult;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class SearchResultViewModel extends ViewModel {

    private final MutableLiveData<String> mQuery = new MutableLiveData<>();

    @Inject
    public SearchResultViewModel() {
    }

    public MutableLiveData<String> getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        mQuery.setValue(query);
    }

    public boolean isEmpty() {
        return mQuery.getValue() == null || mQuery.getValue().isEmpty();
    }

}
