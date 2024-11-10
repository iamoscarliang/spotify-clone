package com.oscarliang.spotifyclone.core.analytics;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

public class StubAnalyticsLogger implements AnalyticsLogger {

    @Inject
    public StubAnalyticsLogger() {
    }

    @Override
    public void logEvent(String event, List<AnalyticsParam> params) {
        Log.d(
                "StubAnalyticsLogger",
                "Analytics event: " + event + ", params: " + params.toString()
        );
    }

}