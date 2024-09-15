package com.oscarliang.spotifyclone.feature.category;

import androidx.annotation.NonNull;

import java.util.Objects;

public class CategoryQuery {

    public final String categoryId;
    public final int count;

    public CategoryQuery(
            String categoryId,
            int count
    ) {
        this.categoryId = categoryId;
        this.count = count;
    }

    public boolean isEmpty() {
        return categoryId == null || categoryId.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryQuery query = (CategoryQuery) o;
        return count == query.count
                && Objects.equals(categoryId, query.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, count);
    }

    @NonNull
    @Override
    public String toString() {
        return "CategoryQuery{" +
                "categoryId='" + categoryId + '\'' +
                ", count=" + count +
                '}';
    }

}