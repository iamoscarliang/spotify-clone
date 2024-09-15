package com.oscarliang.spotifyclone.core.testing.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelUtil {

    private ViewModelUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends ViewModel> ViewModelProvider.Factory createFor(final T model) {
        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <V extends ViewModel> V create(@NonNull Class<V> modelClass) {
                if (modelClass.isAssignableFrom(model.getClass())) {
                    return (V) model;
                }
                throw new IllegalArgumentException("unexpected model class " + modelClass);
            }
        };
    }

}