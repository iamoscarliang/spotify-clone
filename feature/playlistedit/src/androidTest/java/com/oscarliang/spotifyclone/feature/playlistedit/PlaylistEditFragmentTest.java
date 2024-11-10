package com.oscarliang.spotifyclone.feature.playlistedit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_URL;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusics;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createPlaylist;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.testing.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;
import com.oscarliang.spotifyclone.core.ui.action.ActionController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.List;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.CompletableSubject;

@RunWith(AndroidJUnit4.class)
public class PlaylistEditFragmentTest {

    private NavController navController;
    private ActionController actionController;
    private PlaylistEditViewModel viewModel;

    private final BehaviorSubject<Result<Pair<Playlist, List<Music>>>> result = BehaviorSubject.create();
    private final CompletableSubject updateState = CompletableSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        actionController = mock(ActionController.class);
        viewModel = mock(PlaylistEditViewModel.class);
        when(viewModel.getResult()).thenReturn(result);
        when(viewModel.updatePlaylist(any(), any(), any())).thenReturn(updateState);

        FragmentScenario<PlaylistEditFragment> scenario = FragmentScenario.launchInContainer(
                PlaylistEditFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        PlaylistEditFragment fragment = new PlaylistEditFragment();
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
        onView(withId(R.id.edit_playlist_name)).check(matches(withText("foo")));
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

    @SuppressWarnings("unchecked")
    @Test
    public void testSave() {
        ArgumentCaptor<Playlist> playlist = ArgumentCaptor.forClass(Playlist.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<String>> musicIds = ArgumentCaptor.forClass(List.class);

        result.onNext(Result.success(
                Pair.create(
                        createPlaylist(UNKNOWN_ID, "foo", UNKNOWN_URL, asList("bar0", "bar1")),
                        createMusics(2, "bar", UNKNOWN_TITLE)
                )
        ));
        onView(withId(R.id.edit_playlist_name)).perform(replaceText("foobar"));
        onView(withId(R.id.btn_save)).perform(click());

        verify(viewModel).updatePlaylist(playlist.capture(), name.capture(), musicIds.capture());
        assertEquals("foo", playlist.getValue().getName());
        assertEquals("foobar", name.getValue());
        assertEquals(2, musicIds.getValue().size());
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_music);
    }

}