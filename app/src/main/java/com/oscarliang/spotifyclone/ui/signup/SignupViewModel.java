package com.oscarliang.spotifyclone.ui.signup;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.domain.usecase.user.SignupUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.Objects;

import javax.inject.Inject;

public class SignupViewModel extends ViewModel {

    private final LiveData<Event<Resource<AuthResult>>> mSignupState;

    @VisibleForTesting
    final MutableLiveData<UserId> mUserId = new MutableLiveData<>();

    @Inject
    public SignupViewModel(SignupUseCase signupUseCase) {
        mSignupState = Transformations.switchMap(mUserId, userId -> {
            if (userId == null || userId.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return signupUseCase.execute(userId.mEmail, userId.mPassword);
            }
        });
    }

    public LiveData<Event<Resource<AuthResult>>> getSignupState() {
        return mSignupState;
    }

    public void signup(String email, String password) {
        mUserId.setValue(new UserId(email, password));
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
