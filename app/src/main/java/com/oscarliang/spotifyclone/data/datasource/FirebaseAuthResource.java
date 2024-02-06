package com.oscarliang.spotifyclone.data.datasource;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

public abstract class FirebaseAuthResource<ResultType> {

    private final MutableLiveData<Event<Resource<ResultType>>> mResult = new MutableLiveData<>();

    public FirebaseAuthResource() {
        init();
    }

    public LiveData<Event<Resource<ResultType>>> getLiveData() {
        return mResult;
    }

    private void init() {
        // Update LiveData to loading state
        setValue(new Event<>(Resource.loading(null)));
        Task<ResultType> result = createCall();

        // Add callback to observe changing from network
        result.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                setValue(new Event<>(Resource.success(null)));
            } else {
                onFetchFailed();
                Exception e = task.getException();
                setValue(new Event<>(Resource.error(e != null ? e.getMessage() : "Unknown error!",
                        null)));
            }
        });
    }

    @MainThread
    protected void setValue(Event<Resource<ResultType>> newValue) {
        if (mResult.getValue() != newValue) {
            mResult.setValue(newValue);
        }
    }

    @NonNull
    @MainThread
    protected abstract Task<ResultType> createCall();

    protected void onFetchFailed() {
    }

}
