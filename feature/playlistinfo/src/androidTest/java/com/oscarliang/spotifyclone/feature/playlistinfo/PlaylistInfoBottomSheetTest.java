package com.oscarliang.spotifyclone.feature.playlistinfo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_URL;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;
import com.oscarliang.spotifyclone.core.ui.action.Action;
import com.oscarliang.spotifyclone.core.ui.action.ActionController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

@RunWith(AndroidJUnit4.class)
public class PlaylistInfoBottomSheetTest {

    private NavController navController;
    private ActionController actionController;
    private PlaylistInfoViewModel viewModel;

    private final BehaviorSubject<Result<Playlist>> result = BehaviorSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        actionController = mock(ActionController.class);
        viewModel = mock(PlaylistInfoViewModel.class);
        when(viewModel.getResult()).thenReturn(result);

        FragmentScenario<PlaylistInfoBottomSheet> scenario = FragmentScenario.launchInContainer(
                PlaylistInfoBottomSheet.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        PlaylistInfoBottomSheet fragment = new PlaylistInfoBottomSheet();
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
    public void testLoaded() {
        result.onNext(Result.success(createPlaylist(UNKNOWN_ID, "foo", UNKNOWN_URL, asList("bar0", "bar1"))));
        onView(withId(R.id.layout_loading)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_playlist)).check(matches(withText("foo")));
        onView(withId(R.id.text_music_count)).check(matches(withText("2")));
    }

    @Test
    public void testNavigate() {
        result.onNext(Result.success(createPlaylist("foo", UNKNOWN_TITLE, UNKNOWN_URL, emptyList())));
        onView(withId(R.id.btn_edit_playlist)).perform(click());
        verify(navController).navigate((Uri.parse("android-app://playlistEditFragment/foo")));
    }

    @Test
    public void testAction() {
        result.onNext(Result.success(createPlaylist("foo", UNKNOWN_TITLE, UNKNOWN_URL, emptyList())));
        onView(withId(R.id.btn_delete_playlist)).perform(click());
        verify(actionController).sendAction(new Action(Action.Type.SHOW_DELETE_PLAYLIST_DIALOG, "foo"));
    }

}