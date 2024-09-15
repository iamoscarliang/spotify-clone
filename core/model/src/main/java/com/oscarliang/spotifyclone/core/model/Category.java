package com.oscarliang.spotifyclone.core.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Category {

    private String id;
    private String name;
    private String color;
    private String imageUrl;

    public Category(
            String id,
            String name,
            String color,
            String imageUrl
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(name, category.name)
                && Objects.equals(color, category.color)
                && Objects.equals(imageUrl, category.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, imageUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

}