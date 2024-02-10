package com.oscarliang.spotifyclone.ui.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.domain.usecase.user.LoginUseCase;
import com.oscarliang.spotifyclone.domain.usecase.user.ResetPasswordUseCase;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class LoginViewModelTest {

    private LoginUseCase mLoginUseCase;
    private ResetPasswordUseCase mResetPasswordUseCase;
    private LoginViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mLoginUseCase = mock(LoginUseCase.class);
        mResetPasswordUseCase = mock(ResetPasswordUseCase.class);
        mViewModel = new LoginViewModel(mLoginUseCase, mResetPasswordUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getLoginState());
        assertNotNull(mViewModel.getResetPasswordState());
        verify(mLoginUseCase, never()).execute(anyString(), anyString());
        verify(mResetPasswordUseCase, never()).execute(anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.login("foo", "bar");
        mViewModel.resetPassword("abc");
        verify(mLoginUseCase, never()).execute(anyString(), anyString());
        verify(mResetPasswordUseCase, never()).execute(anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> email = ArgumentCaptor.forClass(String.class);

        mViewModel.getLoginState().observeForever(mock(Observer.class));
        mViewModel.login("foo", "bar");
        mViewModel.getResetPasswordState().observeForever(mock(Observer.class));
        mViewModel.resetPassword("abc");

        verify(mLoginUseCase, times(1)).execute(id.capture(), id.capture());
        verify(mResetPasswordUseCase, times(1)).execute(email.capture());
        assertEquals(Arrays.asList("foo", "bar"), id.getAllValues());
        assertEquals("abc", email.getValue());
    }

    @Test
    public void nullUser() {
        mViewModel.login("foo", "bar");
        mViewModel.login(null, null);
        Observer<Event<Resource<AuthResult>>> observer = mock(Observer.class);
        mViewModel.getLoginState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void nullEmail() {
        mViewModel.resetPassword("foo");
        mViewModel.resetPassword(null);
        Observer<Event<Resource<Void>>> observer = mock(Observer.class);
        mViewModel.getResetPasswordState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void login() {
        Observer<LoginViewModel.UserId> observer = mock(Observer.class);
        mViewModel.mUserId.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.login("foo", "bar");
        verify(observer).onChanged(new LoginViewModel.UserId("foo", "bar"));
    }

    @Test
    public void sendResetEmail() {
        Observer<String> observer = mock(Observer.class);
        mViewModel.mResetPasswordEmail.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.resetPassword("foo");
        verify(observer).onChanged("foo");
    }

}
