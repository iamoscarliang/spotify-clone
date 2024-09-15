package com.oscarliang.spotifyclone.core.ui.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public class AutoClearedValue<T> {

    private T value;

    public AutoClearedValue(Fragment fragment, T value) {
        fragment.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                fragment.getViewLifecycleOwnerLiveData().observe(fragment, viewLifecycleOwner -> {
                            Lifecycle lifecycle = viewLifecycleOwner.getLifecycle();
                            if (viewLifecycleOwner != null && lifecycle != null) {
                                lifecycle.addObserver(new DefaultLifecycleObserver() {
                                    @Override
                                    public void onDestroy(@NonNull LifecycleOwner owner) {
                                        AutoClearedValue.this.value = null;
                                    }
                                });
                            }
                        }
                );
            }
        });
        this.value = value;
    }

    public T get() {
        if (value == null) {
            throw new IllegalStateException("should never call auto-cleared-value get when it might not be available");
        }
        return value;
    }

}