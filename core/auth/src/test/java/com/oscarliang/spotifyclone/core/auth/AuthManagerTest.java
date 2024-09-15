package com.oscarliang.spotifyclone.core.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AuthManagerTest {

    private FirebaseAuth auth;
    private AuthManager authManager;

    @Before
    public void setUp() {
        auth = mock(FirebaseAuth.class);
        authManager = new AuthManager(auth);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetUserIdWithoutSignIn() {
        when(auth.getCurrentUser()).thenReturn(null);
        authManager.getUserId();
    }

    @Test(expected = IllegalStateException.class)
    public void testSignOutWithoutSignIn() {
        when(auth.getCurrentUser()).thenReturn(null);
        authManager.signOut();
    }

}