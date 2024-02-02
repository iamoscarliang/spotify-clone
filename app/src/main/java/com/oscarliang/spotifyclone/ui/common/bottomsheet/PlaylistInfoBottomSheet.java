package com.oscarliang.spotifyclone.ui.common.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.BottomSheetPlaylistInfoBinding;
import com.oscarliang.spotifyclone.domain.model.Playlist;

public class PlaylistInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String PLAYLIST = "playlist";

    private Playlist mPlaylist;

    private BottomSheetPlaylistInfoBinding mBinding;
    private PlaylistInfoBottomSheetCallback mCallback;

    public PlaylistInfoBottomSheet() {
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
            // Instantiate the callback so you can send events to the host
            mCallback = (PlaylistInfoBottomSheetCallback) getParentFragment();
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface
            throw new ClassCastException(getParentFragment().toString() + " must implement callback!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = BottomSheetPlaylistInfoBinding.inflate(inflater, container, false);
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
        mBinding.btnEditPlaylist.setOnClickListener(v -> {
            mCallback.onEditPlaylistClick(mPlaylist);
            dismiss();
        });
        mBinding.btnDeletePlaylist.setOnClickListener(v -> {
            mCallback.onDeletePlaylistClick(mPlaylist);
            dismiss();
        });

        initMusic();
    }

    private void initMusic() {
        Glide.with(getContext())
                .load(mPlaylist.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(mBinding.layoutPlaylistInfoItem.imagePlaylist);
        mBinding.layoutPlaylistInfoItem.textPlaylist.setText(mPlaylist.getName());
        mBinding.layoutPlaylistInfoItem.textMusicCount.setText(mPlaylist.getMusicIds().size() + " musics");
    }

    public interface PlaylistInfoBottomSheetCallback {

        void onEditPlaylistClick(Playlist playlist);

        void onDeletePlaylistClick(Playlist playlist);

    }

}
