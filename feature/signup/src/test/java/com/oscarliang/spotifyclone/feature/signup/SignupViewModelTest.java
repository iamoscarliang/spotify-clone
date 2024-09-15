package com.oscarliang.spotifyclone.feature.signup;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.core.data.repository.AuthRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SignupViewModelTest {

    private AuthRepository authRepository;
    private SignupViewModel viewModel;

    @Before
    public void setUp() {
        authRepository = mock(AuthRepository.class);
        viewModel = new SignupViewModel(authRepository);
    }

    @Test
    public void testNonNull() {
        assertNotNull(viewModel.getSignupEnable());
        viewModel.getSignupEnable().test().assertValue(false);
    }

    @Test
    public void testSignup() {
        viewModel.signup("foo", "bar", "123");
        verify(authRepository).signup("foo", "bar", "123");
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