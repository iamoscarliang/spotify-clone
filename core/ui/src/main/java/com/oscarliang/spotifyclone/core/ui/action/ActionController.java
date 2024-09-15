package com.oscarliang.spotifyclone.core.ui.action;

import com.oscarliang.spotifyclone.core.common.util.Event;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@Singleton
public class ActionController {

    private final BehaviorSubject<Event<Action>> action = BehaviorSubject.create();

    @Inject
    public ActionController() {
    }

    public Observable<Event<Action>> getAction() {
        return action;
    }

    public void sendAction(Action action) {
        this.action.onNext(new Event<>(action));
    }

}