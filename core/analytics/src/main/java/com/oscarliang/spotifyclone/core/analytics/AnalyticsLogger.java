package com.oscarliang.spotifyclone.core.analytics;

import java.util.List;

public interface AnalyticsLogger {

    void logEvent(String event, List<AnalyticsParam> params);

}