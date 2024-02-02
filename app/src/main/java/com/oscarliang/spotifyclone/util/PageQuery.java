package com.oscarliang.spotifyclone.util;

import java.util.Objects;

public class PageQuery {

    public final String mQuery;
    public final int mResultPerPage;
    public final int mPage;

    public PageQuery(String query, int resultPerPage, int page) {
        mQuery = query;
        mResultPerPage = resultPerPage;
        mPage = page;
    }

    public static PageQuery next(PageQuery currentPage) {
        return new PageQuery(currentPage.mQuery, currentPage.mResultPerPage, currentPage.mPage + 1);
    }

    public boolean isEmpty() {
        return mQuery == null || mQuery.isEmpty()
                || mResultPerPage == 0 || mPage == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageQuery pageQuery = (PageQuery) o;
        return mResultPerPage == pageQuery.mResultPerPage
                && mPage == pageQuery.mPage
                && Objects.equals(mQuery, pageQuery.mQuery);
    }

}
