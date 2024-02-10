package com.oscarliang.spotifyclone.ui.onboard;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OnboardFragmentTest {

    private NavController mNavController;

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        FragmentScenario<OnboardFragment> scenario =
                FragmentScenario.launchInContainer(OnboardFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        return new OnboardFragment();
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void navigateToSignup() {
        onView(withId(R.id.text_signup)).perform(click());
        verify(mNavController).navigate(eq(R.id.action_onboardFragment_to_signupFragment));
    }

    @Test
    public void navigateToLogin() {
        onView(withId(R.id.text_login)).perform(click());
        verify(mNavController).navigate(eq(R.id.action_onboardFragment_to_loginFragment));
    }

}
