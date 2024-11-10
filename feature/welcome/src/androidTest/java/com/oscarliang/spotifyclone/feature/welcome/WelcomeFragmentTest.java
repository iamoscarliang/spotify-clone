package com.oscarliang.spotifyclone.feature.welcome;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WelcomeFragmentTest {

    private NavController navController;

    @Before
    public void setUp() {
        navController = mock(NavController.class);

        FragmentScenario<WelcomeFragment> scenario = FragmentScenario.launchInContainer(
                WelcomeFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        WelcomeFragment fragment = new WelcomeFragment();
                        fragment.analyticsLogger = new NoOpAnalyticsLogger();
                        return fragment;
                    }
                }
        );
        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.getView(), navController)
        );
    }

    @Test
    public void testNavigate() {
        onView(withId(R.id.btn_signup)).perform(click());
        verify(navController).navigate(eq(Uri.parse("android-app://signupFragment")), any());

        onView(withId(R.id.btn_login)).perform(click());
        verify(navController).navigate(eq(Uri.parse("android-app://loginFragment")), any());
    }

}