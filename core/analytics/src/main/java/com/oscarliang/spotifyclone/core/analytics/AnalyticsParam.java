package com.oscarliang.spotifyclone.core.analytics;

import androidx.annotation.NonNull;

import java.util.Objects;

public class AnalyticsParam {

    public static final String SCREEN_NAME = "screen_name";
    public static final String MUSIC_ID = "music_id";
    public static final String MUSIC_TITLE = "music_title";

    public String key;
    public String value;

    public AnalyticsParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalyticsParam param = (AnalyticsParam) o;
        return Objects.equals(key, param.key)
                && Objects.equals(value, param.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "AnalyticsParam{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

}