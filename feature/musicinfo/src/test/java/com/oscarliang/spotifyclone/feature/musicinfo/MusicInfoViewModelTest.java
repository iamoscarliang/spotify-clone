package com.oscarliang.spotifyclone.feature.musicinfo;

import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusic;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;

@RunWith(JUnit4.class)
public class MusicInfoViewModelTest {

    private MusicRepository musicRepository;
    private MusicInfoViewModel viewModel;

    @Before
    public void setUp() {
        musicRepository = mock(MusicRepository.class);
        viewModel = new MusicInfoViewModel(musicRepository);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getResult());
        verify(musicRepository, never()).getCachedMusicsById(any());
    }

    @Test
    public void testBlankQuery() {
        viewModel.setMusicId("foo");
        viewModel.setMusicId("");
        viewModel.getResult().test().assertEmpty();
    }

    @Test
    public void testQueryWithoutObserver() {
        viewModel.setMusicId("foo");
        verify(musicRepository, never()).getCachedMusicsById(any());
    }

    @Test
    public void testQueryWhenObserved() {
        viewModel.getResult().subscribe();
        viewModel.setMusicId("foo");
        verify(musicRepository).getCachedMusicsById("foo");
    }

    @Test
    public void testChangeQuery() {
        when(musicRepository.getCachedMusicsById(any())).thenReturn(Single.never());

        viewModel.getResult().subscribe();
        viewModel.setMusicId("foo");
        viewModel.setMusicId("bar");

        verify(musicRepository).getCachedMusicsById("foo");
        verify(musicRepository).getCachedMusicsById("bar");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSameQuery() {
        Observer<String> observer = mock(Observer.class);
        viewModel.musicId.subscribe(observer);
        verify(observer, never()).onNext(any());

        viewModel.setMusicId("foo");
        verify(observer).onNext("foo");
        reset(observer);

        viewModel.setMusicId("foo");
        verify(observer, never()).onNext(any());
        viewModel.setMusicId("bar");
        verify(observer).onNext("bar");
    }

    @Test
    public void testSearch() {
        when(musicRepository.getCachedMusicsById("foo"))
                .thenReturn(Single.just(createMusic("foo", "bar")));

        viewModel.setMusicId("foo");
        viewModel.getResult().test().assertValues(
                Result.loading(),
                Result.success(createMusic("foo", "bar"))
        );
    }

}