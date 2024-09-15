package com.oscarliang.spotifyclone.core.common.util;

public class Event<T> {

    private final T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    public T getContentIfNotHandled() {
        // Returns the content and prevents its use again
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public T peekContent() {
        // Returns the content, even if it's already been handled
        return content;
    }

}