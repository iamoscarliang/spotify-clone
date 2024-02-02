package com.oscarliang.spotifyclone.util;

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

}
