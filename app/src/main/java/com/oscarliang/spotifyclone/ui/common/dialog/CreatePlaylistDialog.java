package com.oscarliang.spotifyclone.ui.common.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.oscarliang.spotifyclone.databinding.DialogCreatePlaylistBinding;

public class CreatePlaylistDialog extends DialogFragment {

    private DialogCreatePlaylistBinding mBinding;
    private onCreatePlaylistClickListener mListener;

    public CreatePlaylistDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verify that the host fragment implements the callback interface
        try {
            // Instantiate the listener so you can send events to the host
            mListener = (onCreatePlaylistClickListener) getParentFragment();
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface
            throw new ClassCastException(getParentFragment().toString() + " must implement listener!");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DialogCreatePlaylistBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDialogAction();
        showSoftKeyBoard();
    }

    private void initDialogAction() {
        mBinding.editPlaylistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                mBinding.btnCreate.setEnabled(charSequence != null && charSequence.length() > 0);
            }
        });


        mBinding.btnCancel.setOnClickListener(v -> dismiss());
        mBinding.btnCreate.setOnClickListener(v -> {
            mListener.onCreatePlaylistClick(mBinding.editPlaylistName.getText().toString().trim());
            dismiss();
        });
        mBinding.btnCreate.setEnabled(false);
    }

    private void showSoftKeyBoard() {
        mBinding.editPlaylistName.post(() -> {
            mBinding.editPlaylistName.requestFocus();
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.editPlaylistName)
                    .show(WindowInsetsCompat.Type.ime());
        });
    }

    public interface onCreatePlaylistClickListener {

        void onCreatePlaylistClick(String playlistName);

    }

}
