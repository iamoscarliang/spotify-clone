package com.oscarliang.spotifyclone.core.data.repository;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createCategories;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createCategory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.core.network.api.CategoryService;
import com.oscarliang.spotifyclone.core.network.model.CategoryEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class DefaultCategoryRepositoryTest {

    private CategoryService service;
    private DefaultCategoryRepository repository;

    @Before
    public void setUp() {
        service = mock(CategoryService.class);
        repository = new DefaultCategoryRepository(service);
    }

    @Test
    public void testGetCategoryById() {
        when(service.getCategoryById(eq("foo"), any()))
                .thenReturn(Single.just(createCategoryEntity("foo", "bar")));
        repository.getCategoryById("foo").test()
                .assertValue(createCategory("foo", "bar"));
    }

    @Test
    public void testGetCategoryByIdError() {
        when(service.getCategoryById(eq("foo"), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getCategoryById("foo").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testGetAllCategories() {
        when(service.getAllCategories(eq(Query.Direction.ASCENDING), any()))
                .thenReturn(Single.just(createCategoryEntities(10, "foo", "bar")));
        repository.getAllCategories(Query.Direction.ASCENDING).test()
                .assertValue(createCategories(10, "foo", "bar"));
    }

    @Test
    public void testGetAllCategoriesError() {
        when(service.getAllCategories(eq(Query.Direction.ASCENDING), any()))
                .thenReturn(Single.error(new Exception("idk")));
        repository.getAllCategories(Query.Direction.ASCENDING).test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    private CategoryEntity createCategoryEntity(String id, String name) {
        return new CategoryEntity(id, name, "", "");
    }

    private List<CategoryEntity> createCategoryEntities(int count, String id, String name) {
        List<CategoryEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createCategoryEntity(id + i, name + i));
        }
        return entities;
    }

}