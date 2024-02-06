package com.oscarliang.spotifyclone.data.datasource;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oscarliang.spotifyclone.util.Resource;

public abstract class FirestoreGetDocumentResource<ResultType> {

    private final Class<?> mClazz;

    private final MutableLiveData<Resource<ResultType>> mResult = new MutableLiveData<>();

    public FirestoreGetDocumentResource(Class<?> clazz) {
        mClazz = clazz;
        init();
    }

    public LiveData<Resource<ResultType>> getLiveData() {
        return mResult;
    }

    private void init() {
        // Update LiveData to loading state
        setValue(Resource.loading(null));
        DocumentReference document = createCall();
        document.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // Convert the response to result type
                        DocumentSnapshot response = task.getResult();
                        ResultType newData = (ResultType) response.toObject(mClazz);

                        if (newData != null) {
                            setValue(Resource.success(newData));
                        } else {
                            setValue(Resource.error("Document not found!", null));
                        }
                    } else {
                        onFetchFailed();
                        Exception e = task.getException();
                        setValue(Resource.error(e != null ? e.getMessage() : "Unknown error!", null));
                    }
                });
    }

    @MainThread
    protected void setValue(Resource<ResultType> newValue) {
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
