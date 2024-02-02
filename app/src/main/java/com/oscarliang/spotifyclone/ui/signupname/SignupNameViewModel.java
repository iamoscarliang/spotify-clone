package com.oscarliang.spotifyclone.ui.signupname;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.domain.usecase.user.UpdateUserNameUseCase;
import com.oscarliang.spotifyclone.util.AbsentLiveData;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class SignupNameViewModel extends ViewModel {

    private final LiveData<Event<Resource<Void>>> mUpdateProfileState;

    private final MutableLiveData<String> mName = new MutableLiveData<>();

    @Inject
    public SignupNameViewModel(UpdateUserNameUseCase updateUserNameUseCase) {
        mUpdateProfileState = Transformations.switchMap(mName, name -> {
            if (name == null || name.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return updateUserNameUseCase.execute(name);
            }
        });
    }

    public LiveData<Event<Resource<Void>>> getUpdateProfileState() {
        return mUpdateProfileState;
    }

    public void setName(String name) {
        mName.setValue(name);
    }

}
