package com.oscarliang.spotifyclone.util;

public abstract class NextPageHandler {

    public void loadPage() {
        if (isFirstPage()) {
            loadFirstPage();
        }

        if (!hasMoreResult()) {
            onQueryExhausted();
        }

        loadResult();
    }

    protected abstract boolean isFirstPage();

    protected abstract void loadFirstPage();

    protected abstract boolean hasMoreResult();

    protected abstract void loadResult();

    protected abstract void onQueryExhausted();

}
