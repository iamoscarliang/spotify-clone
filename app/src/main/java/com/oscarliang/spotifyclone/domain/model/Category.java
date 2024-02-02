package com.oscarliang.spotifyclone.domain.model;

import java.util.Objects;

public class Category {

    private String mName;
    private String mColor;

    public Category(String name, String color) {
        mName = name;
        mColor = color;
    }

    public Category() {
        // Needed for Firebase
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
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
        return Objects.equals(mName, category.mName) && Objects.equals(mColor, category.mColor);
    }

}
