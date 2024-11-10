package com.oscarliang.spotifyclone.feature.player;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusic;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Collections.emptyList;

import android.net.Uri;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.player.PlaybackState;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

@RunWith(AndroidJUnit4.class)
public class PlayerFragmentTest {

    private NavController navController;
    private MusicPlayer musicPlayer;
    private PlayerViewModel viewModel;

    private final BehaviorSubject<Result<Music>> result = BehaviorSubject.create();
    private final BehaviorSubject<PlaybackState> playbackState = BehaviorSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        musicPlayer = mock(MusicPlayer.class);
        when(musicPlayer.getPlaybackState()).thenReturn(playbackState);
        viewModel = mock(PlayerViewModel.class);
        when(viewModel.getResult()).thenReturn(result);

        FragmentScenario<PlayerFragment> scenario = FragmentScenario.launchInContainer(
                PlayerFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        PlayerFragment fragment = new PlayerFragment();
                        fragment.musicPlayer = musicPlayer;
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
        result.onNext(Result.success(createMusic(UNKNOWN_ID, "foo")));
        onView(withId(R.id.layout_loading)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_title)).check(matches(withText("foo")));
    }

    @Test
    public void testLoadPlayback() {
        playbackState.onNext(
                new PlaybackState("foo", 60000, 0, false, false, emptyList())
        );
        onView(withId(R.id.text_duration)).check(matches(withText("01:00")));
    }

    @Test
    public void testChangePlayback() {
        playbackState.onNext(
                new PlaybackState("foo", 60000, 0, false, false, emptyList())
        );
        onView(withId(R.id.text_current_time)).check(matches(withText("00:00")));
        onView(withId(R.id.text_duration)).check(matches(withText("01:00")));

        onView(withId(R.id.seekbar)).perform(setProgress(30000));
        onView(withId(R.id.text_current_time)).check(matches(withText("00:30")));
    }

    @Test
    public void testNavigate() {
        result.onNext(Result.success(createMusic("foo", UNKNOWN_TITLE)));

        onView(withId(R.id.btn_more)).perform(click());
        verify(navController).navigate((Uri.parse("android-app://musicInfoBottomSheet/foo")));
        reset(navController);
    }

    public static ViewAction setProgress(int progress) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Perform setting the progress on a SeekBar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
        };
    }

}