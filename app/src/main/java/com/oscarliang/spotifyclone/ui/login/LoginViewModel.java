package com.oscarliang.spotifyclone.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.domain.usecase.user.LoginUserUseCase;
import com.oscarliang.spotifyclone.domain.usecase.user.ResetPasswordUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    private final LiveData<Event<Resource<AuthResult>>> mLoginUserState;
    private final LiveData<Event<Resource<Void>>> mResetPasswordState;

    private final MutableLiveData<LoginUserRequest> mRequest = new MutableLiveData<>();
    private final MutableLiveData<String> mResetPasswordEmail = new MutableLiveData<>();

    @Inject
    public LoginViewModel(LoginUserUseCase loginUserUseCase,
                          ResetPasswordUseCase resetPasswordUseCase) {
        mLoginUserState = Transformations.switchMap(mRequest, request -> {
            if (request == null
                    || request.mEmail == null || request.mEmail.isEmpty()
                    || request.mPassword == null || request.mPassword.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return loginUserUseCase.execute(request.mEmail, request.mPassword);
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

    public LiveData<Event<Resource<AuthResult>>> getLoginUserState() {
        return mLoginUserState;
    }

    public LiveData<Event<Resource<Void>>> getResetPasswordState() {
        return mResetPasswordState;
    }

    public void loginUser(String email, String password) {
        mRequest.setValue(new LoginUserRequest(email, password));
    }

    public void resetPassword(String email) {
        mResetPasswordEmail.setValue(email);
    }

    private static class LoginUserRequest {

        private final String mEmail;
        private final String mPassword;

        public LoginUserRequest(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

    }

}
