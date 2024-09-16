package com.oscarliang.spotifyclone.feature.signup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.data.repository.AuthRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class SignupViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final Observable<Boolean> isSignupEnable;

    private final BehaviorSubject<String> name = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> email = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> password = BehaviorSubject.createDefault("");

    @Inject
    public SignupViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        this.isSignupEnable = Observable.combineLatest(
                name, email, password,
                (name, email, password) -> !name.isEmpty() && !email.isEmpty() && !password.isEmpty()
        );
    }

    public Observable<Boolean> getSignupEnable() {
        return isSignupEnable;
    }

    public void setName(@NonNull String name) {
        this.name.onNext(name);
    }

    public void setEmail(@NonNull String email) {
        this.email.onNext(email);
    }

    public void setPassword(@NonNull String password) {
        this.password.onNext(password);
    }

    public Completable signup(String name, String email, String password) {
        return authRepository.signup(name, email, password);
    }

}