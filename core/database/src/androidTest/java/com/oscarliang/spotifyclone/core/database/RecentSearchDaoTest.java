package com.oscarliang.spotifyclone.core.database;

import static org.junit.Assert.assertEquals;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.database.dao.RecentSearchDao;
import com.oscarliang.spotifyclone.core.database.model.RecentSearchEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RecentSearchDaoTest {

    private SpotifyDatabase database;
    private RecentSearchDao dao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                SpotifyDatabase.class
        ).build();
        dao = database.getRecentSearchDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndRead() {
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("a", 0)).blockingAwait();
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("b", 3)).blockingAwait();
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("c", 1)).blockingAwait();
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("d", 2)).blockingAwait();

        List<RecentSearchEntity> entities = dao.getRecentSearches(2).blockingFirst();
        assertEquals(
                entities,
                Arrays.asList(
                        new RecentSearchEntity("b", 3),
                        new RecentSearchEntity("d", 2)
                )
        );
    }

    @Test
    public void testInsertAndClear() {
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("a", 0)).blockingAwait();
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("b", 3)).blockingAwait();
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("c", 1)).blockingAwait();
        dao.insertOrReplaceRecentSearch(new RecentSearchEntity("d", 2)).blockingAwait();

        List<RecentSearchEntity> entitiesBefore = dao.getRecentSearches(10).blockingFirst();
        assertEquals(entitiesBefore.size(), 4);

        dao.clearRecentSearches().blockingAwait();
        List<RecentSearchEntity> entitiesAfter = dao.getRecentSearches(10).blockingFirst();
        assertEquals(entitiesAfter.size(), 0);
    }

}