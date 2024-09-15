package com.oscarliang.spotifyclone.feature.search;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createCategories;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.firestore.Query;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.CategoryRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class SearchViewModelTest {

    private CategoryRepository categoryRepository;
    private SearchViewModel viewModel;

    @Before
    public void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        viewModel = new SearchViewModel(categoryRepository);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(categoryRepository, never()).getAllCategories(any());
    }

    @Test
    public void testQueryWithoutObserver() {
        viewModel.onToggleSort();
        verify(categoryRepository, never()).getAllCategories(any());
    }

    @Test
    public void testQueryWhenObserved() {
        when(categoryRepository.getAllCategories(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        verify(categoryRepository).getAllCategories(Query.Direction.ASCENDING);
        viewModel.onToggleSort();
        verify(categoryRepository).getAllCategories(Query.Direction.DESCENDING);
    }

    @Test
    public void testSearch() {
        when(categoryRepository.getAllCategories(any()))
                .thenReturn(Single.just(createCategories(10, "foo", "bar")));

        // Test emit success state after loading state
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(createCategories(10, "foo", "bar"))
        );
    }

    @Test
    public void testSearchError() {
        when(categoryRepository.getAllCategories(any()))
                .thenReturn(Single.error(new Exception("idk")));

        // Test emit error state after loading state
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.error("idk", null)
        );
    }

    @Test
    public void testRetry() {
        when(categoryRepository.getAllCategories(any())).thenReturn(Single.never());

        // Test retry without observer
        viewModel.retry();
        verify(categoryRepository, never()).getAllCategories(any());

        // Test emit current query when subscribed
        viewModel.getResult().subscribe();
        verify(categoryRepository).getAllCategories(any());
        reset(categoryRepository);

        // Test retry current query
        viewModel.retry();
        verify(categoryRepository).getAllCategories(any());
    }

}