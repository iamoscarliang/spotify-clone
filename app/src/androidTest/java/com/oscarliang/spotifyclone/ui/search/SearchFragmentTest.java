package com.oscarliang.spotifyclone.ui.search;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.domain.model.Category;
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
public class SearchFragmentTest {

    private NavController mNavController;
    private SearchViewModel mViewModel;

    private final MutableLiveData<Resource<List<Category>>> mCategories = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(SearchViewModel.class);
        when(mViewModel.getCategories()).thenReturn(mCategories);

        FragmentScenario<SearchFragment> scenario =
                FragmentScenario.launchInContainer(SearchFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        SearchFragment fragment = new SearchFragment();
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
        mCategories.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_category)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mCategories.postValue(Resource.success(Arrays.asList(TestUtil.createCategory("foo"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo"))));
        onView(withId(R.id.shimmer_layout_category)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mCategories.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_category)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to load")));
    }

    @Test
    public void navigateToCategory() {
        mCategories.postValue(Resource.success(Arrays.asList(TestUtil.createCategory("foo"))));
        onView(withText("foo")).perform(click());
        verify(mNavController).navigate(eq(R.id.action_searchFragment_to_categoryFragment), any(Bundle.class));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_category);
    }

}
