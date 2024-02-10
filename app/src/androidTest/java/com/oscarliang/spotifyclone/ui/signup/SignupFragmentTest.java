package com.oscarliang.spotifyclone.ui.signup;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
public class SignupFragmentTest {

    private NavController mNavController;
    private SignupViewModel mViewModel;

    private final MutableLiveData<Event<Resource<AuthResult>>> mSignupState = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(SignupViewModel.class);
        when(mViewModel.getSignupState()).thenReturn(mSignupState);

        FragmentScenario<SignupFragment> scenario =
                FragmentScenario.launchInContainer(SignupFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        SignupFragment fragment = new SignupFragment();
                        fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mSignupState.postValue(new Event<>(Resource.loading(null)));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_next)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testLogin() {
        mSignupState.postValue(new Event<>(Resource.success(null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        verify(mNavController).navigate(eq(R.id.action_signupFragment_to_signupNameFragment));
    }

    @Test
    public void testErrorLogin() {
        mSignupState.postValue(new Event<>(Resource.error("Failed to sign up", null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to sign up")));
    }

}
