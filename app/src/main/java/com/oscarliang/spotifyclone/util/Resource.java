package com.oscarliang.spotifyclone.util;

import java.util.Objects;

public class Resource<T> {

    public final State mState;
    public final T mData;
    public final String mMessage;

    public enum State {
        SUCCESS,
        ERROR,
        LOADING
    }

    //--------------------------------------------------------
    // Constructors
    //--------------------------------------------------------
    public Resource(State state, T data, String message) {
        mState = state;
        mData = data;
        mMessage = message;
    }
    //========================================================

    //--------------------------------------------------------
    // Static methods
    //--------------------------------------------------------
    public static <T> Resource<T> success(T data) {
        return new Resource<>(State.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, T data) {
        return new Resource<>(State.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(State.LOADING, data, null);
    }
    //========================================================

    //--------------------------------------------------------
    // Overriding methods
    //--------------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resource<?> resource = (Resource<?>) o;
        return mState == resource.mState
                && Objects.equals(mData, resource.mData)
                && Objects.equals(mMessage, resource.mMessage);
    }
    //========================================================

}
