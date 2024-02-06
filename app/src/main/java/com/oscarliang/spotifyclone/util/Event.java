package com.oscarliang.spotifyclone.util;

import java.util.Objects;

public class Event<T> {

    private final T mContent;

    private boolean mHasBeenHandled = false;

    public Event(T content) {
        mContent = content;
    }

    public T getContentIfNotHandled() {
        if (mHasBeenHandled) {
            return null;
        } else {
            // Returns the content and prevents its use again
            mHasBeenHandled = true;
            return mContent;
        }
    }

    public T peekContent() {
        // Returns the content, even if it's already been handled
        return mContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event<?> event = (Event<?>) o;
        return mHasBeenHandled == event.mHasBeenHandled
                && Objects.equals(mContent, event.mContent);
    }

}
