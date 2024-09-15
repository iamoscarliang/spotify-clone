package com.oscarliang.spotifyclone.feature.searchresult;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusics;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.ui.util.LoadMoreState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.TestScheduler;

@RunWith(JUnit4.class)
public class MusicResultViewModelTest {

    private MusicRepository musicRepository;
    private MusicResultViewModel viewModel;

    private final TestScheduler scheduler = new TestScheduler();

    @Before
    public void setUp() {
        musicRepository = mock(MusicRepository.class);
        viewModel = new MusicResultViewModel(musicRepository, scheduler);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(musicRepository, never()).search(any(), anyInt());
    }

    @Test
    public void testBlankQuery() {
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        viewModel.setQuery(new SearchResultQuery("", 10));
        scheduler.triggerActions();
        viewModel.getResult().test().assertEmpty();
    }

    @Test
    public void testQuery() {
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(musicRepository).search("foo", 10);
    }

    @Test
    public void testChangeQuery() {
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        viewModel.setQuery(new SearchResultQuery("bar", 10));
        scheduler.triggerActions();

        verify(musicRepository).search("foo", 10);
        verify(musicRepository).search("bar", 10);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSameQuery() {
        Observer<SearchResultQuery> observer = mock(Observer.class);
        viewModel.query.subscribe(observer);
        verify(observer, never()).onNext(any());

        viewModel.setQuery(new SearchResultQuery("foo", 10));
        verify(observer).onNext(new SearchResultQuery("foo", 10));
        reset(observer);

        viewModel.setQuery(new SearchResultQuery("foo", 10));
        verify(observer, never()).onNext(any());
        viewModel.setQuery(new SearchResultQuery("bar", 10));
        verify(observer).onNext(new SearchResultQuery("bar", 10));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearch() {
        when(musicRepository.search("foo", 10))
                .thenReturn(Single.just(createMusics(10, "foo", "bar")));

        // We use a mock observer to verify function
        // call, since result stream is a stateflow,
        // which only cache the latest value
        Observer<Result<List<Music>>> observer = mock(Observer.class);
        viewModel.getResult().subscribe(observer);

        // Test emit success state after loading state
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(observer).onNext(Result.loading());
        verify(observer).onNext(Result.success(createMusics(10, "foo", "bar")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchError() {
        when(musicRepository.search("foo", 10))
                .thenReturn(Single.error(new Exception("idk")));

        Observer<Result<List<Music>>> observer = mock(Observer.class);
        viewModel.getResult().subscribe(observer);

        // Test emit error state after loading state
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(observer).onNext(Result.loading());
        verify(observer).onNext(Result.error("idk", null));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchNextPage() {
        when(musicRepository.search("foo", 10))
                .thenReturn(Single.just(createMusics(10, "foo", "bar")));
        when(musicRepository.searchNextPage("foo", 10))
                .thenReturn(Single.just(createMusics(10, "foo1", "bar1")));

        Observer<Result<List<Music>>> observer = mock(Observer.class);
        viewModel.getResult().subscribe(observer);

        // Test emit success state after loading state
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(observer).onNext(Result.loading());
        verify(observer).onNext(Result.success(createMusics(10, "foo", "bar")));

        // Test emit success state with previous page's data
        viewModel.loadNextPage();
        scheduler.triggerActions();
        verify(observer).onNext(Result.success(createMusics(20, "foo", "bar")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchNextPageError() {
        when(musicRepository.search("foo", 10))
                .thenReturn(Single.just(createMusics(10, "foo", "bar")));
        when(musicRepository.searchNextPage("foo", 10))
                .thenReturn(Single.error(new Exception("idk")));

        Observer<Result<List<Music>>> observer = mock(Observer.class);
        viewModel.getResult().subscribe(observer);

        // Test emit success state after loading state
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(observer).onNext(Result.loading());
        verify(observer).onNext(Result.success(createMusics(10, "foo", "bar")));

        // Test emit error state with previous page's data
        viewModel.loadNextPage();
        scheduler.triggerActions();
        verify(observer).onNext(Result.error("idk", createMusics(10, "foo", "bar")));
    }

    @Test
    public void testRetry() {
        when(musicRepository.search(any(), anyInt())).thenReturn(Single.never());

        // Test retry without query
        viewModel.retry();
        scheduler.triggerActions();
        verify(musicRepository, never()).search(any(), anyInt());

        // Test emit current query
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(musicRepository).search("foo", 10);
        reset(musicRepository);

        // Test retry current query
        viewModel.retry();
        verify(musicRepository).search("foo", 10);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadMore() {
        when(musicRepository.search("foo", 10))
                .thenReturn(Single.just(createMusics(10, "foo", "bar")));
        when(musicRepository.searchNextPage("foo", 10))
                .thenReturn(Single.just(createMusics(5, "foo1", "bar1")));

        Observer<LoadMoreState> observer = mock(Observer.class);
        viewModel.getLoadMoreState().subscribe(observer);

        // Test the initial idling state
        scheduler.triggerActions();
        verify(observer).onNext(LoadMoreState.IDLING);
        reset(observer);

        // Test success fetch and has next page
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(observer).onNext(LoadMoreState.IDLING);

        // Test don't fetch when running
        viewModel.loadNextPage();
        verify(observer).onNext(LoadMoreState.RUNNING);
        reset(observer);
        viewModel.loadNextPage();
        verify(observer, never()).onNext(any());

        // Test success fetch and no more pages
        scheduler.triggerActions();
        verify(observer).onNext(LoadMoreState.NO_MORE);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadMoreError() {
        when(musicRepository.search("foo", 10))
                .thenReturn(Single.just(createMusics(10, "foo", "bar")));
        when(musicRepository.searchNextPage("foo", 10))
                .thenReturn(Single.error(new Exception("idk")));

        Observer<LoadMoreState> observer = mock(Observer.class);
        viewModel.getLoadMoreState().subscribe(observer);

        // Test the initial idling state
        scheduler.triggerActions();
        verify(observer).onNext(LoadMoreState.IDLING);
        reset(observer);

        // Test success fetch and has next page
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        scheduler.triggerActions();
        verify(observer).onNext(LoadMoreState.IDLING);

        // Test error fetch
        viewModel.loadNextPage();
        verify(observer).onNext(LoadMoreState.RUNNING);
        scheduler.triggerActions();
        verify(observer).onNext(LoadMoreState.ERROR);
    }

    @Test
    public void testClear() {
        // We always have only 1 active stream,
        // so it won't update the current page and
        // all the previous page's data when retry
        assertEquals(viewModel.disposables.size(), 0);
        viewModel.setQuery(new SearchResultQuery("foo", 10));
        assertEquals(viewModel.disposables.size(), 1);
        viewModel.loadNextPage();
        assertEquals(viewModel.disposables.size(), 1);
        viewModel.onCleared();
        assertEquals(viewModel.disposables.size(), 0);
    }

}