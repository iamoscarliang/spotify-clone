package com.oscarliang.spotifyclone.core.data.mapper;

import static org.junit.Assert.assertEquals;

import com.oscarliang.spotifyclone.core.database.model.RecentSearchEntity;
import com.oscarliang.spotifyclone.core.model.RecentSearch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RecentSearchMapperTest {

    @Test
    public void testMapToModel() {
        RecentSearchEntity entity = new RecentSearchEntity(
                "query",
                123L
        );
        RecentSearch model = RecentSearchMapper.map(entity);

        assertEquals(model.getQuery(), "query");
        assertEquals(model.getQueriedTime(), 123L);
    }

    @Test
    public void testMapToEntity() {
        RecentSearch model = new RecentSearch(
                "query",
                123L
        );
        RecentSearchEntity entity = RecentSearchMapper.map(model);

        assertEquals(entity.getQuery(), "query");
        assertEquals(entity.getQueriedTime(), 123L);
    }

}