package com.oscarliang.spotifyclone.feature.login;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.core.auth.AuthManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LoginViewModelTest {

    private AuthManager authManager;
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        authManager = mock(AuthManager.class);
        viewModel = new LoginViewModel(authManager);
    }

    @Test
    public void testLogin() {
        viewModel.login("foo", "bar");
        verify(authManager).login("foo", "bar");
    }

    @Test
    public void testResetPassword() {
        viewModel.resetPassword("foo");
        verify(authManager).resetPassword("foo");
    }

    @Test
    public void testLoginWithInCompleteData() {
        viewModel.setEmail("foo");
        viewModel.getLoginEnable().test().assertValue(false);
    }

    @Test
    public void testSignupWithCompleteData() {
        viewModel.setEmail("foo");
        viewModel.setPassword("123");
        viewModel.getLoginEnable().test().assertValue(true);
    }

    @Test
    public void testSignupWithBlankData() {
        viewModel.setEmail("foo");
        viewModel.setPassword("");
        viewModel.getLoginEnable().test().assertValue(false);
        viewModel.setPassword("123");
        viewModel.getLoginEnable().test().assertValue(true);
    }

}