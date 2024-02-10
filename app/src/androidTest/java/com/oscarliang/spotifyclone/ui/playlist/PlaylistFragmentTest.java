package com.oscarliang.spotifyclone.ui.playlist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaMetadata;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.TestUtil;
import com.oscarliang.spotifyclone.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PlaylistFragmentTest {

    private NavController mNavController;
    private PlaylistViewModel mPlaylistViewModel;
    private MainViewModel mMainViewModel;

    private final MutableLiveData<Resource<List<Music>>> mMusics = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mDeletePlaylistState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mPlaylistViewModel = mock(PlaylistViewModel.class);
        mMainViewModel = mock(MainViewModel.class);
        when(mPlaylistViewModel.getPlaylistMusics()).thenReturn(mMusics);
        when(mPlaylistViewModel.getDeletePlaylistState()).thenReturn(mDeletePlaylistState);
        when(mMainViewModel.getPlaying()).thenReturn(mIsPlaying);

        FragmentScenario<PlaylistFragment> scenario =
                FragmentScenario.launchInContainer(PlaylistFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        PlaylistFragment fragment = new PlaylistFragment();
                        fragment.mFactory = ViewModelUtil.createFor(Arrays.asList(mPlaylistViewModel, mMainViewModel));
                        fragment.mPlaylist = TestUtil.createPlaylist("foo", "bar", null, Arrays.asList("FOO", "BAR"));
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mMusics.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_music)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mMusics.postValue(Resource.success(Arrays.asList(TestUtil.createMusic("FOO", "BAR"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("BAR"))));
        onView(withId(R.id.shimmer_layout_music)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mMusics.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_music)).check(matches(isDisplayed()));
        onView(withId(R.id.layout_loading_state_playlist)).check(matches(isDisplayed()));
        onView(withId(R.id.text_message)).check(matches(withText("Failed to load")));
    }

    @Test
    public void testLoadingDeletePlaylist() {
        mDeletePlaylistState.postValue(new Event<>(Resource.loading(null)));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeletePlaylist() {
        mDeletePlaylistState.postValue(new Event<>(Resource.success(null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        verify(mNavController).navigateUp();
    }

    @Test
    public void testErrorDeletePlaylist() {
        mDeletePlaylistState.postValue(new Event<>(Resource.error("Failed to delete", null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to delete")));
    }

    @Test
    public void testPlaying() {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle("foo")
                .build();
        when(mMainViewModel.getPlaylistMetadata()).thenReturn(metadata);
        mIsPlaying.postValue(false);
        onView(withId(R.id.fab_play)).check(matches(withTagValue(equalTo(R.drawable.ic_play))));
        mIsPlaying.postValue(true);
        onView(withId(R.id.fab_play)).check(matches(withTagValue(equalTo(R.drawable.ic_pause))));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_music);
    }

}
