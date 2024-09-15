package com.oscarliang.spotifyclone.core.data.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oscarliang.spotifyclone.core.auth.api.AuthService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;

@RunWith(JUnit4.class)
public class DefaultAuthRepositoryTest {

    private AuthService service;
    private DefaultAuthRepository repository;

    @Before
    public void setUp() {
        service = mock(AuthService.class);
        repository = new DefaultAuthRepository(service);
    }

    @Test
    public void testSignup() {
        when(service.signup(any(), any())).thenReturn(Completable.complete());
        when(service.setUserName(any())).thenReturn(Completable.complete());
        repository.signup("foo", "bar", "123").test().assertComplete();
    }

    @Test
    public void testSignupError() {
        when(service.signup(any(), any())).thenReturn(Completable.error(new Exception("idk")));
        when(service.setUserName(any())).thenReturn(Completable.complete());
        repository.signup("foo", "bar", "123").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testSignupNameError() {
        when(service.signup(any(), any())).thenReturn(Completable.complete());
        when(service.setUserName(any())).thenReturn(Completable.error(new Exception("idk")));
        repository.signup("foo", "bar", "123").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testLogin() {
        when(service.login(any(), any())).thenReturn(Completable.complete());
        repository.login("foo", "123").test().assertComplete();
    }

    @Test
    public void testLoginError() {
        when(service.login(any(), any())).thenReturn(Completable.error(new Exception("idk")));
        repository.login("foo", "123").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

    @Test
    public void testResetPassword() {
        when(service.resetPassword(any())).thenReturn(Completable.complete());
        repository.resetPassword("foo").test().assertComplete();
    }

    @Test
    public void testResetPasswordError() {
        when(service.resetPassword(any())).thenReturn(Completable.error(new Exception("idk")));
        repository.resetPassword("foo").test()
                .assertError(throwable -> Objects.equals(throwable.getMessage(), "idk"));
    }

}