package com.oscarliang.spotifyclone.ui.searchresult;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SearchResultViewModelTest {

    private SearchResultViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mViewModel = new SearchResultViewModel();
    }

    @Test
    public void testNull() {
        assertTrue(mViewModel.isEmpty());
    }

    @Test
    public void testEmpty() {
        mViewModel.setQuery("foo");
        assertFalse(mViewModel.isEmpty());
    }

    @Test
    public void resetQuery() {
        Observer<String> observer = mock(Observer.class);
        mViewModel.mQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setQuery("foo");
        verify(observer).onChanged("foo");
        reset(observer);
        mViewModel.setQuery("foo");
        verifyNoMoreInteractions(observer);
        mViewModel.setQuery("bar");
        verify(observer).onChanged("bar");
    }

}
