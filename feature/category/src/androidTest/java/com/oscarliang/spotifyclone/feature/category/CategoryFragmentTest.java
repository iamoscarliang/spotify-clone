package com.oscarliang.spotifyclone.feature.category;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.core.testing.util.EspressoTestUtil.nestedScrollTo;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_ID;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.UNKNOWN_TITLE;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createCategory;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createMusics;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Collections.singletonList;

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
import com.oscarliang.spotifyclone.core.common.util.Result;
import com.oscarliang.spotifyclone.core.model.Category;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.testing.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.core.testing.util.TestUtil;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;
import com.oscarliang.spotifyclone.core.ui.util.LoadMoreState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

@RunWith(AndroidJUnit4.class)
public class CategoryFragmentTest {

    private NavController navController;
    private MusicPlayer musicPlayer;
    private CategoryViewModel viewModel;

    private final BehaviorSubject<Result<Pair<Category, List<Music>>>> result = BehaviorSubject.create();
    private final BehaviorSubject<LoadMoreState> loadMoreState = BehaviorSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        musicPlayer = mock(MusicPlayer.class);
        viewModel = mock(CategoryViewModel.class);
        when(viewModel.getResult()).thenReturn(result);
        when(viewModel.getLoadMoreState()).thenReturn(loadMoreState);

        FragmentScenario<CategoryFragment> scenario = FragmentScenario.launchInContainer(
                CategoryFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        CategoryFragment fragment = new CategoryFragment();
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
    public void testSuccess() {
        result.onNext(Result.success(
                Pair.create(
                        createCategory(UNKNOWN_ID, "foo"),
                        createMusics(2, UNKNOWN_ID, "bar")
                )
        ));
        onView(withId(R.id.layout_loading)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_category_name)).check(matches(withText("foo")));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar0"))));
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("bar1"))));
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
    public void testLoadNextPage() {
        result.onNext(Result.success(
                Pair.create(
                        createCategory(UNKNOWN_ID, "foo"),
                        createMusics(20, UNKNOWN_ID, "bar")
                )
        ));
        onView(listMatcher().atPosition(19)).perform(nestedScrollTo());
        onView(listMatcher().atPosition(19)).check(matches(isDisplayed()));
        verify(viewModel).loadNextPage();
    }

    @Test
    public void testLoadMoreState() {
        loadMoreState.onNext(LoadMoreState.IDLING);
        onView(withId(R.id.progressbar_more)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_message)).check(matches(not(isDisplayed())));
        loadMoreState.onNext(LoadMoreState.RUNNING);
        onView(withId(R.id.progressbar_more)).check(matches(isDisplayed()));
        onView(withId(R.id.text_message)).check(matches(not(isDisplayed())));
        loadMoreState.onNext(LoadMoreState.NO_MORE);
        onView(withId(R.id.progressbar_more)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_message)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigate() {
        result.onNext(Result.success(
                Pair.create(
                        createCategory(UNKNOWN_ID, UNKNOWN_TITLE),
                        singletonList(TestUtil.createMusic("foo", UNKNOWN_TITLE))
                )
        ));

        onView(listMatcher().atPosition(0)).perform(longClick());
        verify(navController).navigate(Uri.parse("android-app://musicInfoBottomSheet/foo"));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_music);
    }

}