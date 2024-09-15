package com.oscarliang.spotifyclone.core.data.mapper;

import static org.junit.Assert.assertEquals;

import com.oscarliang.spotifyclone.core.model.Category;
import com.oscarliang.spotifyclone.core.network.model.CategoryEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CategoryMapperTest {

    @Test
    public void testMapToModel() {
        CategoryEntity entity = new CategoryEntity(
                "0",
                "name",
                "color",
                "imageUrl"
        );
        Category model = CategoryMapper.map(entity);

        assertEquals(model.getId(), "0");
        assertEquals(model.getName(), "name");
        assertEquals(model.getColor(), "color");
    }

    @Test
    public void testMapToEntity() {
        Category model = new Category(
                "0",
                "name",
                "color",
                "imageUrl"
        );
        CategoryEntity entity = CategoryMapper.map(model);

        assertEquals(entity.getId(), "0");
        assertEquals(entity.getName(), "name");
        assertEquals(entity.getColor(), "color");
    }

}