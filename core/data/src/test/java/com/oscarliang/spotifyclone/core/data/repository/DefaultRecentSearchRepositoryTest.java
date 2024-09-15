package com.oscarliang.spotifyclone.core.data.repository;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createRecentSearches;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.database.dao.RecentSearchDao;
import com.oscarliang.spotifyclone.core.database.model.RecentSearchEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@RunWith(JUnit4.class)
public class DefaultRecentSearchRepositoryTest {

    private RecentSearchDao dao;
    private DefaultRecentSearchRepository repository;

    @Before
    public void setUp() {
        dao = mock(RecentSearchDao.class);
        repository = new DefaultRecentSearchRepository(dao);
    }

    @Test
    public void testGetRecentSearches() {
        when(dao.getRecentSearches(10))
                .thenReturn(Observable.just(createRecentSearchEntities(10, "foo", 0)));
        repository.getRecentSearches(10).test()
                .assertValue(createRecentSearches(10, "foo", 0));
    }

    @Test
    public void testInsertOrReplaceRecentSearch() {
        when(dao.insertOrReplaceRecentSearch(any())).thenReturn(Completable.never());
        long time = System.currentTimeMillis();
        repository.insertOrReplaceRecentSearch("foo").subscribe();
        verify(dao).insertOrReplaceRecentSearch(createRecentSearchEntity("foo", time));
    }

    @Test
    public void testClearRecentSearches() {
        when(dao.clearRecentSearches()).thenReturn(Completable.never());
        repository.clearRecentSearches().subscribe();
        verify(dao).clearRecentSearches();
    }

    private RecentSearchEntity createRecentSearchEntity(String query, long queriedTime) {
        return new RecentSearchEntity(query, queriedTime);
    }

    private List<RecentSearchEntity> createRecentSearchEntities(int count, String query, long queriedTime) {
        List<RecentSearchEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createRecentSearchEntity(query + i, queriedTime + i));
        }
        return entities;
    }

}