package com.oscarliang.spotifyclone.domain.usecase.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ResetPasswordUseCaseTest {

    private UserRepository mRepository;
    private ResetPasswordUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(UserRepository.class);
        mUseCase = new ResetPasswordUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo");
        verify(mRepository).resetPassword("foo");
    }

}
