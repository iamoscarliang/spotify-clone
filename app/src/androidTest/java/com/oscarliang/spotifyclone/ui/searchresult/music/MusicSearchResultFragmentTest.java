package com.oscarliang.spotifyclone.ui.searchresult.music;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.util.EspressoTestUtil.nestedScrollTo;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ApplicationProvider;
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
public class MusicSearchResultFragmentTest {

    private NavController mNavController;
    private MusicSearchResultViewModel mMusicViewModel;
    private MainViewModel mMainViewModel;

    private final MutableLiveData<Resource<List<Music>>> mMusics = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToPlaylistState = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToNewPlaylistState = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mMusicViewModel = mock(MusicSearchResultViewModel.class);
        mMainViewModel = mock(MainViewModel.class);
        when(mMusicViewModel.getMusics()).thenReturn(mMusics);
        when(mMainViewModel.getAddToPlaylistState()).thenReturn(mAddToPlaylistState);
        when(mMainViewModel.getAddToNewPlaylistState()).thenReturn(mAddToNewPlaylistState);

        FragmentScenario<MusicSearchResultFragment> scenario =
                FragmentScenario.launchInContainer(MusicSearchResultFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        MusicSearchResultFragment fragment = new MusicSearchResultFragment();
                        fragment.mFactory = ViewModelUtil.createFor(Arrays.asList(mMusicViewModel, mMainViewModel));
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mMusics.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_search_result)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mMusics.postValue(Resource.success(Arrays.asList(TestUtil.createMusic("foo", "bar"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_search_result)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mMusics.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_search_result)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to load")));
    }

    @Test
    public void loadMore() {
        mMusics.postValue(Resource.success(TestUtil.createMusics(10, "foo", "bar")));
        onView(listMatcher().atPosition(9)).perform(nestedScrollTo());
        onView(listMatcher().atPosition(9)).check(matches(isDisplayed()));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
        verify(mMusicViewModel).loadNextPage();
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

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_search_result);
    }

    private String getString(@StringRes int id, Object... args) {
        return ApplicationProvider.getApplicationContext().getString(id, args);
    }

}
