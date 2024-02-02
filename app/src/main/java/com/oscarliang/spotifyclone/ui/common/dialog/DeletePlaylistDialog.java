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

public class DeletePlaylistDialog extends DialogFragment {

    private static final String PLAYLIST = "playlist";

    private Playlist mPlaylist;

    private DialogDeletePlaylistBinding mBinding;
    private OnConfirmDeletePlaylistClickListener mListener;

    public DeletePlaylistDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylist = getArguments().getParcelable(PLAYLIST);
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
        mBinding = DialogDeletePlaylistBinding.inflate(inflater, container, false);
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
    }

    private void initDialogAction() {
        mBinding.btnCancel.setOnClickListener(v -> dismiss());
        mBinding.btnDelete.setOnClickListener(v -> {
            mListener.onConfirmDeletePlaylistClick(mPlaylist);
            dismiss();
        });
    }

    public interface OnConfirmDeletePlaylistClickListener {

        void onConfirmDeletePlaylistClick(Playlist playlist);

    }

}
