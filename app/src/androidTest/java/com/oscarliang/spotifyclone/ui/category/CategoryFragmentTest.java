package com.oscarliang.spotifyclone.ui.category;

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
public class CategoryFragmentTest {

    private CategoryViewModel mCategoryViewModel;
    private MainViewModel mMainViewModel;

    private final MutableLiveData<Resource<List<Music>>> mMusics = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToPlaylistState = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToNewPlaylistState = new MutableLiveData<>();

    @Before
    public void init() {
        mCategoryViewModel = mock(CategoryViewModel.class);
        mMainViewModel = mock(MainViewModel.class);
        when(mCategoryViewModel.getMusics()).thenReturn(mMusics);
        when(mMainViewModel.getAddToPlaylistState()).thenReturn(mAddToPlaylistState);
        when(mMainViewModel.getAddToNewPlaylistState()).thenReturn(mAddToNewPlaylistState);

        FragmentScenario.launchInContainer(CategoryFragment.class, null, new FragmentFactory() {
            @NonNull
            @Override
            public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                CategoryFragment fragment = new CategoryFragment();
                fragment.mFactory = ViewModelUtil.createFor(Arrays.asList(mCategoryViewModel, mMainViewModel));
                return fragment;
            }
        });
    }

    @Test
    public void testLoading() {
        mMusics.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_music)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mMusics.postValue(Resource.success(Arrays.asList(TestUtil.createMusic("foo", "bar"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_music)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mMusics.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_music)).check(matches(isDisplayed()));
        onView(withId(R.id.layout_loading_state_category)).check(matches(isDisplayed()));
        onView(withId(R.id.text_message)).check(matches(withText("Failed to load")));
    }

    @Test
    public void loadMore() {
        mMusics.postValue(Resource.success(TestUtil.createMusics(10, "foo", "bar")));
        onView(listMatcher().atPosition(9)).perform(nestedScrollTo());
        onView(listMatcher().atPosition(9)).check(matches(isDisplayed()));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
        verify(mCategoryViewModel).loadNextPage();
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
        return new RecyclerViewMatcher(R.id.recycler_view_music);
    }

    private String getString(@StringRes int id, Object... args) {
        return ApplicationProvider.getApplicationContext().getString(id, args);
    }

}
