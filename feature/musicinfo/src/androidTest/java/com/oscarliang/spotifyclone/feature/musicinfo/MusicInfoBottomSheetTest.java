package com.oscarliang.spotifyclone.feature.musicinfo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

@RunWith(AndroidJUnit4.class)
public class MusicInfoBottomSheetTest {

    private NavController navController;
    private MusicInfoViewModel viewModel;

    private final BehaviorSubject<Result<Music>> result = BehaviorSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        viewModel = mock(MusicInfoViewModel.class);
        when(viewModel.getResult()).thenReturn(result);

        FragmentScenario<MusicInfoBottomSheet> scenario = FragmentScenario.launchInContainer(
                MusicInfoBottomSheet.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        MusicInfoBottomSheet fragment = new MusicInfoBottomSheet();
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
    public void testNavigate() {
        result.onNext(Result.success(createMusic("0", UNKNOWN_TITLE, "foo", "bar")));

        onView(withId(R.id.btn_add_to_playlist)).perform(click());
        verify(navController).navigate((Uri.parse("android-app://playlistSelectFragment/0")));
        reset(navController);

        onView(withId(R.id.btn_view_album)).perform(click());
        verify(navController).navigate((Uri.parse("android-app://albumFragment/foo")));
        reset(navController);

        onView(withId(R.id.btn_view_artist)).perform(click());
        verify(navController).navigate((Uri.parse("android-app://artistFragment/bar")));
        reset(navController);
    }

}