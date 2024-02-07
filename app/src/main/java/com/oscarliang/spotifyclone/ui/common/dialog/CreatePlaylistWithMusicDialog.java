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
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.util.AutoClearedValue;

public class CreatePlaylistWithMusicDialog extends DialogFragment {

    private static final String MUSIC_KEY = "music";

    private Music mMusic;

    private AutoClearedValue<DialogCreatePlaylistBinding> mBinding;
    private onCreatePlaylistWithMusicClickListener mListener;

    public CreatePlaylistWithMusicDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(MUSIC_KEY)) {
            mMusic = args.getParcelable(MUSIC_KEY);
        }
        // Verify that the host fragment implements the callback interface
        try {
            // Instantiate the callback so you can send events to the host
            mListener = (onCreatePlaylistWithMusicClickListener) getParentFragment();
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface
            throw new ClassCastException(getParentFragment().toString() + " must implement callback!");
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
        DialogCreatePlaylistBinding viewBinding = DialogCreatePlaylistBinding.inflate(inflater,
                container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDialogAction();
        showSoftKeyBoard();
    }

    private void initDialogAction() {
        mBinding.get().editPlaylistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                mBinding.get().btnCreate.setEnabled(charSequence != null && charSequence.length() > 0);
            }
        });

        mBinding.get().btnCancel.setOnClickListener(v -> dismiss());
        mBinding.get().btnCreate.setOnClickListener(v -> {
            mListener.onCreatePlaylistWithMusicClick(mBinding.get().editPlaylistName.getText().toString().trim(), mMusic);
            dismiss();
        });
        mBinding.get().btnCreate.setEnabled(false);
    }

    private void showSoftKeyBoard() {
        mBinding.get().editPlaylistName.post(() -> {
            mBinding.get().editPlaylistName.requestFocus();
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.get().editPlaylistName)
                    .show(WindowInsetsCompat.Type.ime());
        });
    }

    public interface onCreatePlaylistWithMusicClickListener {

        void onCreatePlaylistWithMusicClick(String playlistName, Music music);

    }

}
