package com.oscarliang.spotifyclone.core.database.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "recent_searches")
public class RecentSearchEntity {

    @NonNull
    @PrimaryKey
    private String query;

    private long queriedTime;

    public RecentSearchEntity(
            @NonNull String query,
            long queriedTime
    ) {
        this.query = query;
        this.queriedTime = queriedTime;
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    public void setQuery(@NonNull String query) {
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
        RecentSearchEntity entity = (RecentSearchEntity) o;
        return queriedTime == entity.queriedTime
                && Objects.equals(query, entity.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, queriedTime);
    }

    @NonNull
    @Override
    public String toString() {
        return "RecentSearchEntity{" +
                "query='" + query + '\'' +
                ", queriedTime=" + queriedTime +
                '}';
    }

}