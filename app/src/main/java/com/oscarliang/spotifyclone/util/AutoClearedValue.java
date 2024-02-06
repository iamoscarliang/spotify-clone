package com.oscarliang.spotifyclone.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class AutoClearedValue<T> {

    private T mValue;

    public AutoClearedValue(Fragment fragment, T value) {
        fragment.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                fragment.getViewLifecycleOwnerLiveData().observe(fragment, viewLifecycleOwner -> {
                            if (viewLifecycleOwner != null && viewLifecycleOwner.getLifecycle() != null) {
                                viewLifecycleOwner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                                    @Override
                                    public void onDestroy(@NonNull LifecycleOwner owner) {
                                        mValue = null;
                                    }
                                });
                            }
                        }
                );
            }
        });
        this.mValue = value;
    }

    public T get() {
        if (mValue == null) {
            throw new IllegalStateException("should never call auto-cleared-value get when it might not be available");
        }
        return mValue;
    }

}
