package com.oscarliang.spotifyclone.ui.login;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.domain.usecase.user.LoginUseCase;
import com.oscarliang.spotifyclone.domain.usecase.user.ResetPasswordUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.Objects;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    private final LiveData<Event<Resource<AuthResult>>> mLoginState;
    private final LiveData<Event<Resource<Void>>> mResetPasswordState;

    @VisibleForTesting
    final MutableLiveData<UserId> mUserId = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<String> mResetPasswordEmail = new MutableLiveData<>();

    @Inject
    public LoginViewModel(LoginUseCase loginUseCase,
                          ResetPasswordUseCase resetPasswordUseCase) {
        mLoginState = Transformations.switchMap(mUserId, userId -> {
            if (userId == null || userId.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return loginUseCase.execute(userId.mEmail, userId.mPassword);
            }
        });
        mResetPasswordState = Transformations.switchMap(mResetPasswordEmail, email -> {
            if (email == null || email.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return resetPasswordUseCase.execute(email);
            }
        });
    }

    public LiveData<Event<Resource<AuthResult>>> getLoginState() {
        return mLoginState;
    }

    public LiveData<Event<Resource<Void>>> getResetPasswordState() {
        return mResetPasswordState;
    }

    public void login(String email, String password) {
        mUserId.setValue(new UserId(email, password));
    }

    public void resetPassword(String email) {
        mResetPasswordEmail.setValue(email);
    }

    @VisibleForTesting
    public static class UserId {

        private final String mEmail;
        private final String mPassword;

        public UserId(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        public boolean isEmpty() {
            return mEmail == null || mPassword == null
                    || mEmail.isEmpty() || mPassword.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            UserId userId = (UserId) o;
            return Objects.equals(mEmail, userId.mEmail)
                    && Objects.equals(mPassword, userId.mPassword);
        }

    }

}
