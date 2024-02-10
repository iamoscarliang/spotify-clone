package com.oscarliang.spotifyclone.ui.playlistedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
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
public class PlaylistEditFragmentTest {

    private NavController mNavController;
    private PlaylistEditViewModel mViewModel;

    private final MutableLiveData<Resource<List<Music>>> mMusics = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mUpdatePlaylistState = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(PlaylistEditViewModel.class);
        when(mViewModel.getPlaylistMusics()).thenReturn(mMusics);
        when(mViewModel.getUpdatePlaylistState()).thenReturn(mUpdatePlaylistState);

        FragmentScenario<PlaylistEditFragment> scenario =
                FragmentScenario.launchInContainer(PlaylistEditFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        PlaylistEditFragment fragment = new PlaylistEditFragment();
                        fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                        fragment.mPlaylist = TestUtil.createPlaylist("0", "foo", "bar", Arrays.asList("FOO", "BAR"));
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
        onView(withId(R.id.edit_playlist_name)).check(matches(withText("foo")));
        onView(withId(R.id.shimmer_layout_music)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mMusics.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_music)).check(matches(isDisplayed()));
        onView(withId(R.id.layout_loading_state_playlist_edit)).check(matches(isDisplayed()));
        onView(withId(R.id.text_message)).check(matches(withText("Failed to load")));
    }

    @Test
    public void testLoadingDeletePlaylist() {
        mUpdatePlaylistState.postValue(new Event<>(Resource.loading(null)));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
    }

    @Test
    public void testUpdatePlaylist() {
        mUpdatePlaylistState.postValue(new Event<>(Resource.success(null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        verify(mNavController).navigateUp();
    }

    @Test
    public void testErrorUpdatePlaylist() {
        mUpdatePlaylistState.postValue(new Event<>(Resource.error("Failed to delete", null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to delete")));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_music);
    }

}
