package com.oscarliang.spotifyclone.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> mCreators;

    //--------------------------------------------------------
    // Constructors
    //--------------------------------------------------------
    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        mCreators = creators;
    }
    //========================================================

    //--------------------------------------------------------
    // Overriding methods
    //--------------------------------------------------------
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<? extends ViewModel> creator = mCreators.get(modelClass);
        if (creator == null) {

            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : mCreators.entrySet()) {

                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }

        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }

        try {
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //========================================================

}
