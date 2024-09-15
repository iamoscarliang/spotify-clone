package com.oscarliang.spotifyclone.core.data.mapper;

import com.oscarliang.spotifyclone.core.model.Category;
import com.oscarliang.spotifyclone.core.network.model.CategoryEntity;

public class CategoryMapper {

    public static Category map(CategoryEntity entity) {
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.getColor(),
                entity.getImageUrl()
        );
    }

    public static CategoryEntity map(Category model) {
        return new CategoryEntity(
                model.getId(),
                model.getName(),
                model.getColor(),
                model.getImageUrl()
        );
    }

}