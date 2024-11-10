package com.oscarliang.spotifyclone.feature.playlist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_URL;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusic;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusics;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;
import com.oscarliang.spotifyclone.core.common.util.Event;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.testing.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;
import com.oscarliang.spotifyclone.core.ui.action.Action;
import com.oscarliang.spotifyclone.core.ui.action.ActionController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@RunWith(AndroidJUnit4.class)
public class PlaylistFragmentTest {

    private NavController navController;
    private MusicPlayer musicPlayer;
    private ActionController actionController;
    private PlaylistViewModel viewModel;

    private final BehaviorSubject<Event<Action>> action = BehaviorSubject.create();
    private final BehaviorSubject<Result<Pair<Playlist, List<Music>>>> result = BehaviorSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        musicPlayer = mock(MusicPlayer.class);
        when(musicPlayer.getPlaybackState()).thenReturn(Observable.never());
        actionController = mock(ActionController.class);
        when(actionController.getAction()).thenReturn(action);
        viewModel = mock(PlaylistViewModel.class);
        when(viewModel.getResult()).thenReturn(result);

        FragmentScenario<PlaylistFragment> scenario = FragmentScenario.launchInContainer(
                PlaylistFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        PlaylistFragment fragment = new PlaylistFragment();
                        fragment.musicPlayer = musicPlayer;
                        fragment.actionController = actionController;
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
        result.onNext(Result.success(
                Pair.create(
                        createPlaylist(UNKNOWN_ID, "foo", UNKNOWN_URL, asList("bar1", "bar0")),
                        createMusics(2, "bar", "foobar")
                )
        ));
        onView(withId(R.id.layout_loading)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_playlist_name)).check(matches(withText("foo")));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foobar1"))));
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("foobar0"))));
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
    public void testNavigate() {
        result.onNext(Result.success(
                Pair.create(
                        createPlaylist(UNKNOWN_ID, UNKNOWN_TITLE, UNKNOWN_URL, Collections.singletonList("foo")),
                        Collections.singletonList(createMusic("foo", UNKNOWN_TITLE))
                )
        ));

        onView(listMatcher().atPosition(0)).perform(longClick());
        verify(navController).navigate((Uri.parse("android-app://musicInfoBottomSheet/foo")));
    }

    @Test
    public void testAction() {
        action.onNext(new Event<>(new Action(Action.Type.SHOW_SNACK_BAR, "idk")));
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("idk")));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_music);
    }

}