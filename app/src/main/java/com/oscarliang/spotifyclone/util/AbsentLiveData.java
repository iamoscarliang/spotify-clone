package com.oscarliang.spotifyclone.util;

import androidx.lifecycle.LiveData;

public class AbsentLiveData<T> extends LiveData<T> {

    //--------------------------------------------------------
    // Constructors
    //--------------------------------------------------------
    private AbsentLiveData() {
        postValue(null);
    }
    //========================================================

    //--------------------------------------------------------
    // Static methods
    //--------------------------------------------------------
    public static <T> LiveData<T> create() {
        return new AbsentLiveData<T>();
    }
    //========================================================

}
