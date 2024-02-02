package com.oscarliang.spotifyclone.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.domain.usecase.user.CreateUserUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class SignupViewModel extends ViewModel {

    private final LiveData<Event<Resource<AuthResult>>> mCreateUserState;

    private final MutableLiveData<CreateUserRequest> mRequest = new MutableLiveData<>();

    @Inject
    public SignupViewModel(CreateUserUseCase createUserUseCase) {
        mCreateUserState = Transformations.switchMap(mRequest, request -> {
            if (request == null
                    || request.mEmail == null || request.mEmail.isEmpty()
                    || request.mPassword == null || request.mPassword.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return createUserUseCase.execute(request.mEmail, request.mPassword);
            }
        });
    }

    public LiveData<Event<Resource<AuthResult>>> getCreateUserState() {
        return mCreateUserState;
    }

    public void createUser(String email, String password) {
        mRequest.setValue(new CreateUserRequest(email, password));
    }

    private static class CreateUserRequest {

        private final String mEmail;
        private final String mPassword;

        public CreateUserRequest(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

    }

}
