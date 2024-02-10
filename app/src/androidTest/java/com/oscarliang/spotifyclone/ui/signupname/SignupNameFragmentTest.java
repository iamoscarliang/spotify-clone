package com.oscarliang.spotifyclone.ui.signupname;

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

import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupNameFragmentTest {

    private NavController mNavController;
    private SignupNameViewModel mViewModel;

    private final MutableLiveData<Event<Resource<Void>>> mUpdateNameState = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(SignupNameViewModel.class);
        when(mViewModel.getUpdateNameState()).thenReturn(mUpdateNameState);

        FragmentScenario<SignupNameFragment> scenario =
                FragmentScenario.launchInContainer(SignupNameFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        SignupNameFragment fragment = new SignupNameFragment();
                        fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mUpdateNameState.postValue(new Event<>(Resource.loading(null)));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_signup)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testLogin() {
        mUpdateNameState.postValue(new Event<>(Resource.success(null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        verify(mNavController).navigate(eq(R.id.action_signupNameFragment_to_homeFragment));
    }

    @Test
    public void testErrorLogin() {
        mUpdateNameState.postValue(new Event<>(Resource.error("Failed to sign up", null)));
        onView(withId(R.id.progressbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btn_signup)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to sign up")));
    }

}
