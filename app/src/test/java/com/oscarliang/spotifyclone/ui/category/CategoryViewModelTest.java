package com.oscarliang.spotifyclone.ui.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.usecase.music.GetMusicsByCategoryUseCase;
import com.oscarliang.spotifyclone.util.PageQuery;
import com.oscarliang.spotifyclone.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class CategoryViewModelTest {

    private GetMusicsByCategoryUseCase mUseCase;
    private CategoryViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mUseCase = mock(GetMusicsByCategoryUseCase.class);
        mViewModel = new CategoryViewModel(mUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getMusics());
        verify(mUseCase, never()).execute(anyString(), anyInt());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setQuery("foo", 10);
        verify(mUseCase, never()).execute(anyString(), anyInt());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> count = ArgumentCaptor.forClass(Integer.class);

        mViewModel.getMusics().observeForever(mock(Observer.class));
        mViewModel.setQuery("foo", 10);

        verify(mUseCase, times(1)).execute(id.capture(), count.capture());
        assertEquals("foo", id.getValue());
        assertEquals(Integer.valueOf(10), count.getValue());
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> count = ArgumentCaptor.forClass(Integer.class);

        mViewModel.getMusics().observeForever(mock(Observer.class));
        mViewModel.setQuery("foo", 10);
        mViewModel.setQuery("bar", 10);

        verify(mUseCase, times(2)).execute(id.capture(), count.capture());
        assertEquals(Arrays.asList("foo", "bar"), id.getAllValues());
        assertEquals(Arrays.asList(10, 10), count.getAllValues());
    }

    @Test
    public void loadMusics() {
        Observer<Resource<List<Music>>> observer = mock(Observer.class);
        mViewModel.getMusics().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(mUseCase);
        mViewModel.setQuery("foo", 10);
        verify(mUseCase).execute("foo", 10);
    }

    @Test
    public void resetQuery() {
        Observer<PageQuery> observer = mock(Observer.class);
        mViewModel.mQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setQuery("foo", 10);
        verify(observer).onChanged(new PageQuery("foo", 10, 1));
        reset(observer);
        mViewModel.setQuery("foo", 10);
        verifyNoMoreInteractions(observer);
        mViewModel.setQuery("bar", 10);
        verify(observer).onChanged(new PageQuery("bar", 10, 1));
    }

    @Test
    public void retry() {
        mViewModel.retry();
        verifyNoMoreInteractions(mUseCase);
        mViewModel.setQuery("foo", 10);
        verifyNoMoreInteractions(mUseCase);
        mViewModel.getMusics().observeForever(mock(Observer.class));
        verify(mUseCase).execute("foo", 10);
        reset(mUseCase);
        mViewModel.retry();
        verify(mUseCase).execute("foo", 10);
    }

    @Test
    public void nullSearch() {
        mViewModel.setQuery("foo", 10);
        mViewModel.setQuery(null, 10);
        Observer<Resource<List<Music>>> observer = mock(Observer.class);
        mViewModel.getMusics().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void empty() {
        Observer<Resource<List<Music>>> result = mock(Observer.class);
        mViewModel.getMusics().observeForever(result);
        mViewModel.loadNextPage();
        verifyNoMoreInteractions(mUseCase);
    }

    @Test
    public void loadNextPage() {
        Observer<PageQuery> observer = mock(Observer.class);
        mViewModel.mQuery.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setQuery("foo", 10);
        verify(observer).onChanged(new PageQuery("foo", 10, 1));
        reset(observer);
        mViewModel.loadNextPage();
        verify(observer).onChanged(new PageQuery("foo", 10, 2));
    }

}
