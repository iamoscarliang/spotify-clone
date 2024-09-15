package com.oscarliang.spotifyclone.core.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.oscarliang.spotifyclone.core.ui.databinding.DialogDeletePlaylistBinding;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;

public class DeletePlaylistDialog extends DialogFragment {

    private static final String PLAYLIST_KEY = "playlistId";

    private String playlistId;

    private AutoClearedValue<DialogDeletePlaylistBinding> binding;
    private OnDeletePlaylistClickListener listener;

    public DeletePlaylistDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(PLAYLIST_KEY)) {
            playlistId = args.getString(PLAYLIST_KEY);
        }
        // Verify that the host fragment implements the callback interface
        try {
            // Instantiate the listener so you can send events to the host
            listener = (OnDeletePlaylistClickListener) getParentFragment();
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
        DialogDeletePlaylistBinding viewBinding = DialogDeletePlaylistBinding
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
        initButton();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initButton() {
        binding.get().btnCancel.setOnClickListener(v -> dismiss());
        binding.get().btnDelete.setOnClickListener(v -> {
            listener.onDeletePlaylistClick(playlistId);
            dismiss();
        });
    }

    public interface OnDeletePlaylistClickListener {

        void onDeletePlaylistClick(String playlistId);

    }

}