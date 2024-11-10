package com.oscarliang.spotifyclone.feature.signup;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.auth.AuthManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.rxjava3.core.Completable;

@RunWith(JUnit4.class)
public class SignupViewModelTest {

    private AuthManager authManager;
    private SignupViewModel viewModel;

    @Before
    public void setUp() {
        authManager = mock(AuthManager.class);
        viewModel = new SignupViewModel(authManager);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getSignupEnable());
        viewModel.getSignupEnable().test().assertValue(false);
    }

    @Test
    public void testSignup() {
        when(authManager.signup(any(), any())).thenReturn(Completable.never());
        when(authManager.setUserName(any())).thenReturn(Completable.never());

        viewModel.signup("foo", "bar", "123");
        verify(authManager).signup("bar", "123");
        verify(authManager).setUserName("foo");
    }

    @Test
    public void testSignupWithInCompleteData() {
        viewModel.setName("foo");
        viewModel.getSignupEnable().test().assertValue(false);
    }

    @Test
    public void testSignupWithCompleteData() {
        viewModel.setName("foo");
        viewModel.setEmail("bar");
        viewModel.setPassword("123");
        viewModel.getSignupEnable().test().assertValue(true);
    }

    @Test
    public void testSignupWithBlankData() {
        viewModel.setName("foo");
        viewModel.setEmail("bar");
        viewModel.setPassword("");
        viewModel.getSignupEnable().test().assertValue(false);
        viewModel.setPassword("123");
        viewModel.getSignupEnable().test().assertValue(true);
    }

}