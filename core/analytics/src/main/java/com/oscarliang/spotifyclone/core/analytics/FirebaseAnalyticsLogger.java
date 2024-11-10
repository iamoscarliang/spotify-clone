package com.oscarliang.spotifyclone.core.analytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import javax.inject.Inject;

public class FirebaseAnalyticsLogger implements AnalyticsLogger {

    private final FirebaseAnalytics analytics;

    @Inject
    public FirebaseAnalyticsLogger(FirebaseAnalytics analytics) {
        this.analytics = analytics;
    }

    @Override
    public void logEvent(String event, List<AnalyticsParam> params) {
        Bundle bundle = new Bundle();
        for (AnalyticsParam param : params) {
            bundle.putString(param.key, param.value);
        }
        analytics.logEvent(event, bundle);
    }

}