package com.oscarliang.spotifyclone.ui.library;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oscarliang.spotifyclone.R;
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
public class LibraryFragmentTest {

    private NavController mNavController;
    private LibraryViewModel mViewModel;

    private final MutableLiveData<Resource<List<Playlist>>> mPlaylists = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddPlaylistState = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mDeletePlaylistState = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(LibraryViewModel.class);
        when(mViewModel.getPlaylists()).thenReturn(mPlaylists);
        when(mViewModel.getAddPlaylistState()).thenReturn(mAddPlaylistState);
        when(mViewModel.getDeletePlaylistState()).thenReturn(mDeletePlaylistState);

        FragmentScenario<LibraryFragment> scenario =
                FragmentScenario.launchInContainer(LibraryFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        LibraryFragment fragment = new LibraryFragment();
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
        mPlaylists.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_playlist)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mPlaylists.postValue(Resource.success(Arrays.asList(TestUtil.createPlaylist("foo", "bar"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_playlist)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mPlaylists.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_playlist)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to load")));
    }

    @Test
    public void testAddPlaylist() {
        mAddPlaylistState.postValue(new Event<>(Resource.success(TestUtil.createPlaylist("foo", "bar"))));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(getString(R.string.playlist_create, "bar"))));
    }

    @Test
    public void testErrorAddPlaylist() {
        mAddPlaylistState.postValue(new Event<>(Resource.error("Failed to add", null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to add")));
    }

    @Test
    public void testDeletePlaylist() {
        mDeletePlaylistState.postValue(new Event<>(Resource.success(TestUtil.createPlaylist("foo", "bar"))));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(getString(R.string.playlist_delete, "bar"))));
    }

    @Test
    public void testErrorDeletePlaylist() {
        mDeletePlaylistState.postValue(new Event<>(Resource.error("Failed to delete", null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to delete")));
    }

    @Test
    public void navigateToPlaylist() {
        mPlaylists.postValue(Resource.success(Arrays.asList(TestUtil.createPlaylist("0", "foo"))));
        onView(withText("foo")).perform(click());
        verify(mNavController).navigate(eq(R.id.action_libraryFragment_to_playlistFragment), any(Bundle.class));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_playlist);
    }

    private String getString(@StringRes int id, Object... args) {
        return ApplicationProvider.getApplicationContext().getString(id, args);
    }

}
