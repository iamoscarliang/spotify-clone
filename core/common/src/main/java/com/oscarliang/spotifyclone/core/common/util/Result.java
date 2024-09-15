package com.oscarliang.spotifyclone.core.common.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Result<T> {

    @NonNull
    public final State state;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public enum State {
        LOADING,
        SUCCESS,
        ERROR,
    }

    public Result(
            @NonNull State state,
            @Nullable T data,
            @Nullable String message
    ) {
        this.state = state;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> loading() {
        return new Result<>(State.LOADING, null, null);
    }

    public static <T> Result<T> success(@Nullable T data) {
        return new Result<>(State.SUCCESS, data, null);
    }

    public static <T> Result<T> error(@Nullable String msg, @Nullable T data) {
        return new Result<>(State.ERROR, data, msg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result<?> result = (Result<?>) o;
        return state == result.state
                && Objects.equals(data, result.data)
                && Objects.equals(message, result.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, data, message);
    }

    @NonNull
    @Override
    public String toString() {
        return "Result{" +
                "state=" + state +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

}