package com.oscarliang.spotifyclone.ui.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    private NavController mNavController;
    private LoginViewModel mViewModel;

    private final MutableLiveData<Event<Resource<AuthResult>>> mLoginState = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Void>>> mResetPasswordState = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(LoginViewModel.class);
        when(mViewModel.getLoginState()).thenReturn(mLoginState);
        when(mViewModel.getResetPasswordState()).thenReturn(mResetPasswordState);

        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        LoginFragment fragment = new LoginFragment();
                        fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mLoginState.postValue(new Event<>(Resource.loading(null)));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testLogin() {
        mLoginState.postValue(new Event<>(Resource.success(null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        verify(mNavController).navigate(eq(R.id.action_loginFragment_to_homeFragment));
    }

    @Test
    public void testErrorLogin() {
        mLoginState.postValue(new Event<>(Resource.error("Failed to log in", null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to log in")));
    }

    @Test
    public void testResetPassword() {
        mResetPasswordState.postValue(new Event<>(Resource.success(null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(getString(R.string.send_reset_password_email))));
    }

    @Test
    public void testErrorResetPassword() {
        mLoginState.postValue(new Event<>(Resource.error("Failed to send reset email", null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to send reset email")));
    }

    private String getString(@StringRes int id, Object... args) {
        return ApplicationProvider.getApplicationContext().getString(id, args);
    }

}
