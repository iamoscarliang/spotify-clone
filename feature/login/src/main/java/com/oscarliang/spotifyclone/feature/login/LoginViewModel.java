package com.oscarliang.spotifyclone.feature.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.oscarliang.spotifyclone.core.auth.AuthManager;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class LoginViewModel extends ViewModel {

    private final AuthManager authManager;
    private final Observable<Boolean> isLoginEnable;

    private final BehaviorSubject<String> email = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> password = BehaviorSubject.createDefault("");

    @Inject
    public LoginViewModel(AuthManager authManager) {
        this.authManager = authManager;
        this.isLoginEnable = Observable.combineLatest(
                email, password,
                (email, password) -> !email.isEmpty() && !password.isEmpty()
        );
    }

    public Observable<Boolean> getLoginEnable() {
        return isLoginEnable;
    }

    public void setEmail(@NonNull String email) {
        this.email.onNext(email);
    }

    public void setPassword(@NonNull String password) {
        this.password.onNext(password);
    }

    public Completable login(String email, String password) {
        return authManager.login(email, password);
    }

    public Completable resetPassword(String email) {
        return authManager.resetPassword(email);
    }

}