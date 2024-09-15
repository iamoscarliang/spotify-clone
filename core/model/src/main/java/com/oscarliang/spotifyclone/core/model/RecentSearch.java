package com.oscarliang.spotifyclone.core.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class RecentSearch {

    private String query;
    private long queriedTime;

    public RecentSearch(
            String query,
            long queriedTime
    ) {
        this.query = query;
        this.queriedTime = queriedTime;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getQueriedTime() {
        return queriedTime;
    }

    public void setQueriedTime(long queriedTime) {
        this.queriedTime = queriedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecentSearch recentSearch = (RecentSearch) o;
        return queriedTime == recentSearch.queriedTime
                && Objects.equals(query, recentSearch.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, queriedTime);
    }

    @NonNull
    @Override
    public String toString() {
        return "RecentSearch{" +
                "query='" + query + '\'' +
                ", queriedTime=" + queriedTime +
                '}';
    }

}