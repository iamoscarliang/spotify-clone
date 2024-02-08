package com.oscarliang.spotifyclone.data.datasource;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.Resource;

public abstract class FirestoreSetDocumentResource<ResultType> {

    private final ResultType mData;

    private final MutableLiveData<Event<Resource<ResultType>>> mResult = new MutableLiveData<>();

    public FirestoreSetDocumentResource(ResultType data) {
        mData = data;
        init();
    }

    public LiveData<Event<Resource<ResultType>>> getLiveData() {
        return mResult;
    }

    private void init() {
        // Update LiveData to loading state
        setValue(new Event<>(Resource.loading(null)));
        DocumentReference document = createCall();

        // Add callback to observe changing from local cache
        document.addSnapshotListener((value, error) -> {
            if (error != null) {
                setValue(new Event<>(Resource.error(error.getMessage(), mData)));
                return;
            }
            if (value != null && value.getMetadata().isFromCache()) {
                setValue(new Event<>(Resource.success(mData)));
            }
        });
        // Add callback to observe changing from network
        document.set(mData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setValue(new Event<>(Resource.success(mData)));
                    } else {
                        onFetchFailed();
                        Exception e = task.getException();
                        setValue(new Event<>(Resource.error(e != null ? e.getMessage() : "Unknown error!", mData)));
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
    protected abstract DocumentReference createCall();

    protected void onFetchFailed() {
    }

}
