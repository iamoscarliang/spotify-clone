package com.oscarliang.spotifyclone.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class ViewModelUtil {

    private ViewModelUtil() {
    }

    public static <T extends ViewModel> ViewModelProvider.Factory createFor(final T model) {
        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <S extends ViewModel> S create(@NonNull Class<S> modelClass) {
                if (modelClass.isAssignableFrom(model.getClass())) {
                    return (S) model;
                }
                throw new IllegalArgumentException("unexpected model class " + modelClass);
            }
        };
    }

    public static <T extends ViewModel> ViewModelProvider.Factory createFor(final List<T> models) {
        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <S extends ViewModel> S create(@NonNull Class<S> modelClass) {
                for (T model : models) {
                    if (modelClass.isAssignableFrom(model.getClass())) {
                        return (S) model;
                    }
                }
                throw new IllegalArgumentException("unexpected model class " + modelClass);
            }
        };
    }

}
