package com.oscarliang.spotifyclone.feature.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.core.analytics.NoOpAnalyticsLogger;
import com.oscarliang.spotifyclone.core.testing.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.CompletableSubject;

@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    private NavController navController;
    private LoginViewModel viewModel;

    private final BehaviorSubject<Boolean> isLoginEnable = BehaviorSubject.create();
    private final CompletableSubject loginState = CompletableSubject.create();

    @Before
    public void setUp() {
        navController = mock(NavController.class);
        viewModel = mock(LoginViewModel.class);
        when(viewModel.getLoginEnable()).thenReturn(isLoginEnable);
        when(viewModel.login(any(), any())).thenReturn(loginState);

        FragmentScenario<LoginFragment> scenario = FragmentScenario.launchInContainer(
                LoginFragment.class,
                null,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(
                            @NonNull ClassLoader classLoader,
                            @NonNull String className
                    ) {
                        LoginFragment fragment = new LoginFragment();
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
    public void testLogin() {
        onView(withId(R.id.edit_email)).perform(typeText("foo"));
        onView(withId(R.id.edit_password)).perform(typeText("123"), closeSoftKeyboard());
        isLoginEnable.onNext(true);

        onView(withId(R.id.btn_login)).perform(click());
        onView(withId(R.id.btn_login)).check(matches(not(isDisplayed())));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
        verify(viewModel).login("foo", "123");
    }

}