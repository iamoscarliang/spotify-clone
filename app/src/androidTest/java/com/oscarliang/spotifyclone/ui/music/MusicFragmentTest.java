package com.oscarliang.spotifyclone.ui.music;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.TestUtil;
import com.oscarliang.spotifyclone.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MusicFragmentTest {

    private MainViewModel mViewModel;

    private final MutableLiveData<Music> mCurrentMusic = new MutableLiveData<>();
    private final MutableLiveData<Long> mDuration = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToPlaylistState = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToNewPlaylistState = new MutableLiveData<>();

    @Before
    public void init() {
        mViewModel = mock(MainViewModel.class);
        when(mViewModel.getCurrentMusic()).thenReturn(mCurrentMusic);
        when(mViewModel.getDuration()).thenReturn(mDuration);
        when(mViewModel.getPlaying()).thenReturn(mIsPlaying);
        when(mViewModel.getAddToPlaylistState()).thenReturn(mAddToPlaylistState);
        when(mViewModel.getAddToNewPlaylistState()).thenReturn(mAddToNewPlaylistState);

        FragmentScenario.launchInContainer(MusicFragment.class, null, new FragmentFactory() {
            @NonNull
            @Override
            public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                MusicFragment fragment = new MusicFragment();
                fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                return fragment;
            }
        });
    }

    @Test
    public void testLoadMusic() {
        mCurrentMusic.postValue(TestUtil.createMusic("0", "foo", "bar"));
        onView(withId(R.id.text_title)).check(matches(withText("foo")));
        onView(withId(R.id.text_artist)).check(matches(withText("bar")));
    }

    @Test
    public void testLoadDuration() {
        mDuration.postValue(90000L);
        onView(withId(R.id.text_duration)).check(matches(withText("01:30")));
    }

    @Test
    public void testPlaying() {
        mIsPlaying.postValue(false);
        onView(withId(R.id.btn_play)).check(matches(withTagValue(equalTo(R.drawable.ic_play_circle))));
        mIsPlaying.postValue(true);
        onView(withId(R.id.btn_play)).check(matches(withTagValue(equalTo(R.drawable.ic_pause_circle))));
    }

    @Test
    public void testAddToPlaylist() {
        mAddToPlaylistState.postValue(new Event<>(Resource.success(TestUtil.createPlaylist("foo", "bar"))));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(getString(R.string.playlist_add, "bar"))));
    }

    @Test
    public void testErrorAddToPlaylist() {
        mAddToPlaylistState.postValue(new Event<>(Resource.error("Failed to add", null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to add")));
    }

    @Test
    public void testAddToNewPlaylist() {
        mAddToNewPlaylistState.postValue(new Event<>(Resource.success(TestUtil.createPlaylist("foo", "bar"))));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(getString(R.string.playlist_add, "bar"))));
    }

    @Test
    public void testErrorAddToNewPlaylist() {
        mAddToNewPlaylistState.postValue(new Event<>(Resource.error("Failed to add", null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to add")));
    }

    private String getString(@StringRes int id, Object... args) {
        return ApplicationProvider.getApplicationContext().getString(id, args);
    }

}
