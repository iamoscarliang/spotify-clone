package com.oscarliang.spotifyclone.feature.searchresult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.data.repository.RecentSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.TestScheduler;

@RunWith(JUnit4.class)
public class SearchResultViewModelTest {

    private RecentSearchRepository recentSearchRepository;
    private SearchResultViewModel viewModel;

    private final TestScheduler scheduler = new TestScheduler();

    @Before
    public void setUp() {
        recentSearchRepository = mock(RecentSearchRepository.class);
        viewModel = new SearchResultViewModel(recentSearchRepository, scheduler);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getQuery());
        viewModel.getQuery().test().assertValue("");
        verify(recentSearchRepository, never()).insertOrReplaceRecentSearch(any());
    }

    @Test
    public void testBlankQuery() {
        when(recentSearchRepository.insertOrReplaceRecentSearch(any())).thenReturn(Completable.never());

        TestObserver<String> observer = viewModel.getQuery().test();
        observer.assertValues("");

        viewModel.setQuery("");
        observer.assertValue("");
        verify(recentSearchRepository, never()).insertOrReplaceRecentSearch(any());
    }

    @Test
    public void testQuery() {
        when(recentSearchRepository.insertOrReplaceRecentSearch(any())).thenReturn(Completable.never());

        TestObserver<String> observer = viewModel.getQuery().test();
        observer.assertValues("");

        viewModel.setQuery("foo");
        observer.assertValues("", "foo");
    }

    @Test
    public void testChangeQuery() {
        when(recentSearchRepository.insertOrReplaceRecentSearch(any())).thenReturn(Completable.never());

        TestObserver<String> observer = viewModel.getQuery().test();
        observer.assertValues("");

        viewModel.setQuery("foo");
        viewModel.setQuery("bar");
        observer.assertValues("", "foo", "bar");
    }

    @Test
    public void testGetRecentSearches() {
        viewModel.getRecentSearches(10);
        verify(recentSearchRepository).getRecentSearches(10);
    }

    @Test
    public void testUpdateRecentSearch() {
        when(recentSearchRepository.insertOrReplaceRecentSearch(any())).thenReturn(Completable.never());
        viewModel.onSearchTriggered("foo");
        verify(recentSearchRepository).insertOrReplaceRecentSearch("foo");
    }

    @Test
    public void testClearRecentSearch() {
        when(recentSearchRepository.clearRecentSearches()).thenReturn(Completable.never());
        viewModel.clearRecentSearches();
        verify(recentSearchRepository).clearRecentSearches();
    }

    @Test
    public void testClear() {
        when(recentSearchRepository.insertOrReplaceRecentSearch(any())).thenReturn(Completable.never());
        when(recentSearchRepository.clearRecentSearches()).thenReturn(Completable.never());

        assertEquals(viewModel.disposables.size(), 0);
        viewModel.onSearchTriggered("foo");
        assertEquals(viewModel.disposables.size(), 1);
        viewModel.clearRecentSearches();
        assertEquals(viewModel.disposables.size(), 2);
        viewModel.onCleared();
        assertEquals(viewModel.disposables.size(), 0);
    }

}