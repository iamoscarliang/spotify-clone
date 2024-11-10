package com.oscarliang.spotifyclone.feature.searchresult;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createRecentSearch;
import static com.oscarliang.spotifyclone.core.testing.util.TestUtil.createRecentSearches;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;
import com.oscarliang.spotifyclone.core.model.RecentSearch;
import com.oscarliang.spotifyclone.core.testing.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

@RunWith(AndroidJUnit4.class)
public class SearchResultFragmentTest {

    private NavController navController;
    private SearchResultFragmentFactory fragmentFactory;
    private SearchResultViewModel viewModel;

    private final BehaviorSubject<List<RecentSearch>> result = BehaviorSubject.create();
    private final BehaviorSubject<String> query = BehaviorSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        fragmentFactory = mock(SearchResultFragmentFactory.class);
        when(fragmentFactory.createFragment(anyInt())).thenReturn(new Fragment());
        viewModel = mock(SearchResultViewModel.class);
        when(viewModel.getRecentSearches(anyInt())).thenReturn(result);
        when(viewModel.getQuery()).thenReturn(query);

        FragmentScenario<SearchResultFragment> scenario = FragmentScenario.launchInContainer(
                SearchResultFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        SearchResultFragment fragment = new SearchResultFragment();
                        fragment.fragmentFactory = fragmentFactory;
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
    public void testGetRecentSearches() {
        result.onNext(createRecentSearches(2, "foo", 0));
        onView(withId(R.id.layout_recent_search)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_clear_search_result)).check(matches(isDisplayed()));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo0"))));
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("foo1"))));
    }

    @Test
    public void testGetEmptyRecentSearches() {
        result.onNext(emptyList());
        onView(withId(R.id.layout_recent_search)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_clear_search_result)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testTriggerRecentSearch() {
        result.onNext(singletonList(createRecentSearch("foo", 0)));
        onView(listMatcher().atPosition(0)).perform(click());
        verify(viewModel).setQuery("foo");
        verify(viewModel).onSearchTriggered("foo");
    }

    @Test
    public void testClearRecentSearch() {
        onView(withId(R.id.btn_clear_search_result)).perform(click());
        verify(viewModel).clearRecentSearches();
    }

    @Test
    public void testSubmitQuery() {
        onView(withId(com.google.android.material.R.id.search_src_text))
                .perform(typeText("foo"), pressImeActionButton());
        verify(viewModel).setQuery("foo");
        verify(viewModel).onSearchTriggered("foo");
    }

    @Test
    public void testClearQuery() {
        onView(withId(com.google.android.material.R.id.search_src_text))
                .perform(typeText("foo"), clearText());
        verify(viewModel).setQuery("");
        verify(viewModel, never()).onSearchTriggered(any());
    }

    @Test
    public void testSearchQuery() {
        query.onNext("foo");
        onView(withId(R.id.layout_recent_search)).check(matches(not(isDisplayed())));
        onView(withId(R.id.pager)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyQuery() {
        query.onNext("");
        onView(withId(R.id.layout_recent_search)).check(matches(isDisplayed()));
        onView(withId(R.id.pager)).check(matches(not(isDisplayed())));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_recent_search);
    }

}