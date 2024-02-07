package com.oscarliang.spotifyclone.ui.common.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.oscarliang.spotifyclone.databinding.DialogDeletePlaylistBinding;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.util.AutoClearedValue;

public class DeletePlaylistDialog extends DialogFragment {

    private static final String PLAYLIST_KEY = "playlist";

    private Playlist mPlaylist;

    private AutoClearedValue<DialogDeletePlaylistBinding> mBinding;
    private OnConfirmDeletePlaylistClickListener mListener;

    public DeletePlaylistDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(PLAYLIST_KEY)) {
            mPlaylist = args.getParcelable(PLAYLIST_KEY);
        }
        // Verify that the host fragment implements the callback interface
        try {
            // Instantiate the listener so you can send events to the host
            mListener = (OnConfirmDeletePlaylistClickListener) getParentFragment();
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
        DialogDeletePlaylistBinding viewBinding = DialogDeletePlaylistBinding.inflate(inflater,
                container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDialogAction();
    }

    private void initDialogAction() {
        mBinding.get().btnCancel.setOnClickListener(v -> dismiss());
        mBinding.get().btnDelete.setOnClickListener(v -> {
            mListener.onConfirmDeletePlaylistClick(mPlaylist);
            dismiss();
        });
    }

    public interface OnConfirmDeletePlaylistClickListener {

        void onConfirmDeletePlaylistClick(Playlist playlist);

    }

}
