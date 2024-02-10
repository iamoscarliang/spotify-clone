package com.oscarliang.spotifyclone.ui.artist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.util.EspressoTestUtil.withCollapsibleToolbarTitle;
import static org.hamcrest.CoreMatchers.is;
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

import com.google.android.material.appbar.CollapsingToolbarLayout;
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
public class ArtistFragmentTest {

    private NavController mNavController;
    private ArtistViewModel mViewModel;

    private final MutableLiveData<Resource<Artist>> mArtist = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Album>>> mAlbums = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(ArtistViewModel.class);
        when(mViewModel.getArtist()).thenReturn(mArtist);
        when(mViewModel.getAlbums()).thenReturn(mAlbums);

        FragmentScenario<ArtistFragment> scenario =
                FragmentScenario.launchInContainer(ArtistFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        ArtistFragment fragment = new ArtistFragment();
                        fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mArtist.postValue(Resource.loading(null));
        mAlbums.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_artist)).check(matches(isDisplayed()));
        onView(withId(R.id.shimmer_layout_album)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mArtist.postValue(Resource.success(TestUtil.createArtist("foo", "bar")));
        onView(isAssignableFrom(CollapsingToolbarLayout.class)).
                check(matches(withCollapsibleToolbarTitle(is("bar"))));
        onView(withId(R.id.shimmer_layout_artist)).check(matches(not(isDisplayed())));

        mAlbums.postValue(Resource.success(Arrays.asList(TestUtil.createAlbum("foo", "bar"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_album)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mArtist.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_artist)).check(matches(isDisplayed()));
        onView(withId(R.id.shimmer_layout_album)).check(matches(isDisplayed()));
        onView(withId(R.id.layout_loading_state_artist)).check(matches(isDisplayed()));
        onView(withId(R.id.text_message)).check(matches(withText("Failed to load")));
    }

    @Test
    public void navigateToAlbum() {
        mAlbums.postValue(Resource.success(Arrays.asList(TestUtil.createAlbum("foo", "bar"))));
        onView(withText("bar")).perform(click());
        verify(mNavController).navigate(eq(R.id.action_to_albumFragment), any(Bundle.class));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_album);
    }

}
