package com.oscarliang.spotifyclone.core.ui.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Action {

    @NonNull
    public final Type type;

    @Nullable
    public final String message;

    public enum Type {
        SHOW_SNACK_BAR,
        SHOW_DELETE_PLAYLIST_DIALOG
    }

    public Action(
            @NonNull Type type,
            @Nullable String message
    ) {
        this.type = type;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Action action = (Action) o;
        return type == action.type
                && Objects.equals(message, action.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message);
    }

    @NonNull
    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }

}