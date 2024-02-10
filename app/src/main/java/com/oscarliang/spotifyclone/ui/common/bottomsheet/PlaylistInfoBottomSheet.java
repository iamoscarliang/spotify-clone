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
import com.oscarliang.spotifyclone.util.AutoClearedValue;

public class PlaylistInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String PLAYLIST_KEY = "playlist";

    private Playlist mPlaylist;

    private AutoClearedValue<BottomSheetPlaylistInfoBinding> mBinding;
    private PlaylistInfoBottomSheetCallback mCallback;

    public PlaylistInfoBottomSheet() {
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
        BottomSheetPlaylistInfoBinding viewBinding = BottomSheetPlaylistInfoBinding.inflate(inflater,
                container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.get().btnEditPlaylist.setOnClickListener(v -> {
            mCallback.onEditPlaylistClick(mPlaylist);
            dismiss();
        });
        mBinding.get().btnDeletePlaylist.setOnClickListener(v -> {
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
                .into(mBinding.get().layoutPlaylistInfoItem.imagePlaylist);
        mBinding.get().layoutPlaylistInfoItem.textPlaylist.setText(mPlaylist.getName());
        int musicCount = mPlaylist.getMusicIds() != null ? mPlaylist.getMusicIds().size() : 0;
        mBinding.get().layoutPlaylistInfoItem.textMusicCount.setText(getString(R.string.playlist_count,
                String.valueOf(musicCount)));
    }

    public interface PlaylistInfoBottomSheetCallback {

        void onEditPlaylistClick(Playlist playlist);

        void onDeletePlaylistClick(Playlist playlist);

    }

}
