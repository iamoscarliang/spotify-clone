package com.oscarliang.spotifyclone.core.ui.dialog;

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

import com.oscarliang.spotifyclone.core.ui.databinding.DialogCreatePlaylistBinding;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;

public class CreatePlaylistDialog extends DialogFragment {

    private AutoClearedValue<DialogCreatePlaylistBinding> binding;
    private onCreatePlaylistClickListener listener;

    public CreatePlaylistDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verify that the host fragment implements the callback interface
        try {
            // Instantiate the listener so you can send events to the host
            listener = (onCreatePlaylistClickListener) getParentFragment();
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface
            throw new ClassCastException(getParentFragment().toString() + " must implement listener!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        DialogCreatePlaylistBinding viewBinding = DialogCreatePlaylistBinding
                .inflate(inflater, container, false);
        binding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        initEditText();
        initButton();
        showSoftKeyBoard();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initEditText() {
        binding.get().editPlaylistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                binding.get().btnCreate.setEnabled(charSequence != null && charSequence.length() > 0);
            }
        });
    }

    private void initButton() {
        binding.get().btnCancel.setOnClickListener(v -> dismiss());
        binding.get().btnCreate.setOnClickListener(v -> {
            listener.onCreatePlaylistClick(binding.get().editPlaylistName.getText().toString().trim());
            dismiss();
        });
        binding.get().btnCreate.setEnabled(false);
    }

    private void showSoftKeyBoard() {
        binding.get().editPlaylistName.post(() -> {
            binding.get().editPlaylistName.requestFocus();
            WindowCompat.getInsetsController(getActivity().getWindow(), binding.get().editPlaylistName)
                    .show(WindowInsetsCompat.Type.ime());
        });
    }

    public interface onCreatePlaylistClickListener {

        void onCreatePlaylistClick(String playlistName);

    }

}