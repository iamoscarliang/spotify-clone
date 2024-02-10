package com.oscarliang.spotifyclone.ui.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.util.EspressoTestUtil.nestedScrollTo;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.model.Artist;
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
public class HomeFragmentTest {

    private NavController mNavController;
    private HomeViewModel mViewModel;

    private final MutableLiveData<Resource<List<Album>>> mLatestAlbums = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Album>>> mAllAlbums = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Artist>>> mAllArtists = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(HomeViewModel.class);
        when(mViewModel.getLatestAlbums()).thenReturn(mLatestAlbums);
        when(mViewModel.getAllAlbums()).thenReturn(mAllAlbums);
        when(mViewModel.getAllArtists()).thenReturn(mAllArtists);

        FragmentScenario<HomeFragment> scenario =
                FragmentScenario.launchInContainer(HomeFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        HomeFragment fragment = new HomeFragment();
                        fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                        fragment.mAuth = mock(FirebaseAuth.class);
                        when(fragment.mAuth.getCurrentUser()).thenReturn(mock(FirebaseUser.class));
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mLatestAlbums.postValue(Resource.loading(null));
        mAllAlbums.postValue(Resource.loading(null));
        mAllArtists.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_latest_album)).check(matches(isDisplayed()));
        onView(withId(R.id.shimmer_layout_all_album)).check(matches(isDisplayed()));
        onView(withId(R.id.shimmer_layout_all_artist)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mLatestAlbums.postValue(Resource.success(Arrays.asList(TestUtil.createAlbum("foo", "bar"))));
        onView(latestListMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_latest_album)).check(matches(not(isDisplayed())));

        mAllAlbums.postValue(Resource.success(Arrays.asList(TestUtil.createAlbum("foo", "bar"))));
        onView(albumListMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_all_album)).check(matches(not(isDisplayed())));

        mAllArtists.postValue(Resource.success(Arrays.asList(TestUtil.createArtist("foo", "bar"))));
        onView(artistListMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_all_artist)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mLatestAlbums.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_latest_album)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to load")));
    }

    @Test
    public void navigateToLatestAlbum() {
        mLatestAlbums.postValue(Resource.success(Arrays.asList(TestUtil.createAlbum("0", "foo"))));
        onView(withText("foo")).perform(click());
        verify(mNavController).navigate(eq(R.id.action_to_albumFragment), any(Bundle.class));
    }

    @Test
    public void navigateToAlbum() {
        mAllAlbums.postValue(Resource.success(Arrays.asList(TestUtil.createAlbum("0", "bar"))));
        onView(withText("bar")).perform(click());
        verify(mNavController).navigate(eq(R.id.action_to_albumFragment), any(Bundle.class));
    }

    @Test
    public void navigateToArtist() {
        mAllArtists.postValue(Resource.success(Arrays.asList(TestUtil.createArtist("0", "boo"))));
        // Scroll to bottom to reveal the whole artist view
        onView(withId(R.id.text_exhausted)).perform(nestedScrollTo());
        onView(withText("boo")).perform(click());
        verify(mNavController).navigate(eq(R.id.action_to_artistFragment), any(Bundle.class));
    }

    private RecyclerViewMatcher latestListMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_latest_album);
    }

    private RecyclerViewMatcher albumListMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_all_album);
    }

    private RecyclerViewMatcher artistListMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_all_artist);
    }

}