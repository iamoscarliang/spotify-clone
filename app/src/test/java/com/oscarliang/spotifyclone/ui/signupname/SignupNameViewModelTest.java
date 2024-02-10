package com.oscarliang.spotifyclone.ui.signupname;

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

import com.oscarliang.spotifyclone.domain.usecase.user.UpdateUserNameUseCase;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

@RunWith(JUnit4.class)
public class SignupNameViewModelTest {

    private UpdateUserNameUseCase mUseCase;
    private SignupNameViewModel mViewModel;

    @Rule
    public InstantTaskExecutorRule mInstantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mUseCase = mock(UpdateUserNameUseCase.class);
        mViewModel = new SignupNameViewModel(mUseCase);
    }

    @Test
    public void testNull() {
        assertNotNull(mViewModel.getUpdateNameState());
        verify(mUseCase, never()).execute(anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setName("foo");
        verify(mUseCase, never()).execute(anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);

        mViewModel.getUpdateNameState().observeForever(mock(Observer.class));
        mViewModel.setName("foo");

        verify(mUseCase, times(1)).execute(name.capture());
        assertEquals("foo", name.getValue());
    }

    @Test
    public void nullName() {
        mViewModel.setName("foo");
        mViewModel.setName(null);
        Observer<Event<Resource<Void>>> observer = mock(Observer.class);
        mViewModel.getUpdateNameState().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void setName() {
        Observer<String> observer = mock(Observer.class);
        mViewModel.mName.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setName("foo");
        verify(observer).onChanged("foo");
    }

}
