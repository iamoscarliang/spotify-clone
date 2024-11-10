package com.oscarliang.spotifyclone.feature.playlistselect;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylists;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Collections.singletonList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;
import com.oscarliang.spotifyclone.core.auth.AuthManager;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.testing.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.List;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.CompletableSubject;

@RunWith(AndroidJUnit4.class)
public class PlaylistSelectFragmentTest {

    private NavController navController;
    private PlaylistSelectViewModel viewModel;

    private final BehaviorSubject<Result<List<Playlist>>> result = BehaviorSubject.create();
    private final CompletableSubject addMusicToPlaylistState = CompletableSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        viewModel = mock(PlaylistSelectViewModel.class);
        when(viewModel.getResult()).thenReturn(result);
        when(viewModel.addMusicToPlaylist(any(), any())).thenReturn(addMusicToPlaylistState);

        FragmentScenario<PlaylistSelectFragment> scenario = FragmentScenario.launchInContainer(
                PlaylistSelectFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        PlaylistSelectFragment fragment = new PlaylistSelectFragment();
                        fragment.musicId = "foo";
                        fragment.authManager = mock(AuthManager.class);
                        fragment.analyticsLogger = new NoOpAnalyticsLogger();
                        fragment.factory = ViewModelUtil.createFor(viewModel);
                        return fragment;
                    }
                }
        );
        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.getView(), navController)
        );
    }

    @Test
    public void testLoading() {
        result.onNext(Result.loading());
        onView(withId(R.id.layout_loading)).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccess() {
        result.onNext(Result.success(createPlaylists(2, UNKNOWN_ID, "foo")));
        onView(withId(R.id.layout_loading)).check(matches(not(isDisplayed())));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo0"))));
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("foo1"))));
    }

    @Test
    public void testError() {
        result.onNext(Result.error("idk", null));
        onView(withId(R.id.layout_loading)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("idk")));
    }

    @Test
    public void testRetry() {
        result.onNext(Result.error("idk", null));
        onView(withId(R.id.layout_loading)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_action)).perform(click());
        verify(viewModel).retry();
    }

    @Test
    public void testAddMusicToPlaylist() {
        ArgumentCaptor<Playlist> playlist = ArgumentCaptor.forClass(Playlist.class);
        ArgumentCaptor<String> musicId = ArgumentCaptor.forClass(String.class);

        result.onNext(Result.success(singletonList(createPlaylist("bar", UNKNOWN_TITLE, singletonList("foo1")))));

        onView(listMatcher().atPosition(0)).perform(click());
        verify(viewModel).addMusicToPlaylist(playlist.capture(), musicId.capture());
        assertEquals(playlist.getValue().getId(), "bar");
        assertEquals(musicId.getValue(), "foo");
    }

    @Test
    public void testAddDuplicatedMusicToPlaylist() {
        result.onNext(Result.success(singletonList(createPlaylist("bar", UNKNOWN_TITLE, singletonList("foo")))));

        onView(listMatcher().atPosition(0)).perform(click());
        verify(viewModel, never()).addMusicToPlaylist(any(), any());
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(isDisplayed()));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_playlist);
    }

}