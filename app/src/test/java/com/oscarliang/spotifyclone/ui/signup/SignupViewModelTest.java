package com.oscarliang.spotifyclone.ui.signup;

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
import com.oscarliang.spotifyclone.domain.usecase.user.SignupUseCase;
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
public class SignupViewModelTest {

    private SignupUseCase mUseCase;
    private SignupViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mUseCase = mock(SignupUseCase.class);
        mViewModel = new SignupViewModel(mUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getSignupState());
        verify(mUseCase, never()).execute(anyString(), anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.signup("foo", "bar");
        verify(mUseCase, never()).execute(anyString(), anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        mViewModel.getSignupState().observeForever(mock(Observer.class));
        mViewModel.signup("foo", "bar");

        verify(mUseCase, times(1)).execute(id.capture(), id.capture());
        assertEquals(Arrays.asList("foo", "bar"), id.getAllValues());
    }

    @Test
    public void nullUser() {
        mViewModel.signup("foo", "bar");
        mViewModel.signup(null, null);
        Observer<Event<Resource<AuthResult>>> observer = mock(Observer.class);
        mViewModel.getSignupState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void signup() {
        Observer<SignupViewModel.UserId> observer = mock(Observer.class);
        mViewModel.mUserId.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.signup("foo", "bar");
        verify(observer).onChanged(new SignupViewModel.UserId("foo", "bar"));
    }

}
