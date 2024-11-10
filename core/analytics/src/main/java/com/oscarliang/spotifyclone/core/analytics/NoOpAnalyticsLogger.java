package com.oscarliang.spotifyclone.core.analytics;

import java.util.List;

import javax.inject.Inject;

public class NoOpAnalyticsLogger implements AnalyticsLogger {

    @Inject
    public NoOpAnalyticsLogger() {
    }

    @Override
    public void logEvent(String event, List<AnalyticsParam> params) {
    }

}