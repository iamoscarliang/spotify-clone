package com.oscarliang.spotifyclone.ui.search;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.oscarliang.spotifyclone.domain.model.Category;
import com.oscarliang.spotifyclone.domain.usecase.category.GetAllCategoriesUseCase;
import com.oscarliang.spotifyclone.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class SearchViewModelTest {

    private GetAllCategoriesUseCase mGetAllCategoriesUseCase;
    private SearchViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mGetAllCategoriesUseCase = mock(GetAllCategoriesUseCase.class);
        mViewModel = new SearchViewModel(mGetAllCategoriesUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getCategories());
        verify(mGetAllCategoriesUseCase, never()).execute();
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setLoad(true);
        verify(mGetAllCategoriesUseCase, never()).execute();
    }

    @Test
    public void fetchWhenObserved() {
        mViewModel.getCategories().observeForever(mock(Observer.class));
        mViewModel.setLoad(true);
        verify(mGetAllCategoriesUseCase, times(1)).execute();
    }

    @Test
    public void loadCategories() {
        Observer<Resource<List<Category>>> observer = mock(Observer.class);
        mViewModel.getCategories().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mGetAllCategoriesUseCase);
        mViewModel.setLoad(true);
        verify(mGetAllCategoriesUseCase).execute();
    }

    @Test
    public void resetState() {
        Observer<Boolean> observer = mock(Observer.class);
        mViewModel.mIsLoad.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setLoad(true);
        verify(observer).onChanged(true);
        reset(observer);
        mViewModel.setLoad(true);
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void refresh() {
        mViewModel.refresh();
        verifyNoMoreInteractions(mGetAllCategoriesUseCase);
        mViewModel.setLoad(true);
        mViewModel.refresh();
        verifyNoMoreInteractions(mGetAllCategoriesUseCase);
        mViewModel.getCategories().observeForever(mock(Observer.class));
        verify(mGetAllCategoriesUseCase).execute();
        reset(mGetAllCategoriesUseCase);
        mViewModel.refresh();
        verify(mGetAllCategoriesUseCase).execute();
    }

}
