package com.oscarliang.spotifyclone.feature.searchresult;

import androidx.annotation.NonNull;

import java.util.Objects;

public class SearchResultQuery {

    public final String input;
    public final int count;

    public SearchResultQuery(
            String input,
            int count
    ) {
        this.input = input;
        this.count = count;
    }

    public boolean isEmpty() {
        return input == null || input.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchResultQuery query = (SearchResultQuery) o;
        return count == query.count
                && Objects.equals(input, query.input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, count);
    }

    @NonNull
    @Override
    public String toString() {
        return "SearchQuery{" +
                "input='" + input + '\'' +
                ", count=" + count +
                '}';
    }

}