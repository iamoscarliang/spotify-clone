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
import com.oscarliang.spotifyclone.databinding.BottomSheetMusicInfoBinding;
import com.oscarliang.spotifyclone.domain.model.Music;

public class MusicInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String MUSIC = "music";

    private Music mMusic;

    private BottomSheetMusicInfoBinding mBinding;
    private MusicInfoBottomSheetCallback mCallback;

    public MusicInfoBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMusic = getArguments().getParcelable(MUSIC);
        }
        // Verify that the host fragment implements the callback interface
        try {
            // Instantiate the callback so you can send events to the host
            mCallback = (MusicInfoBottomSheetCallback) getParentFragment();
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
        mBinding = BottomSheetMusicInfoBinding.inflate(inflater, container, false);
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
        mBinding.btnAddToPlaylist.setOnClickListener(v -> {
            mCallback.onAddToPlaylistClick(mMusic);
            dismiss();
        });
        mBinding.btnViewAlbum.setOnClickListener(v -> {
            mCallback.onViewAlbumClick(mMusic);
            dismiss();
        });
        mBinding.btnViewArtist.setOnClickListener(v -> {
            mCallback.onViewArtistClick(mMusic);
            dismiss();
        });

        initMusic();
    }

    private void initMusic() {
        Glide.with(getContext())
                .load(mMusic.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(mBinding.layoutMusicInfoItem.imageMusic);
        mBinding.layoutMusicInfoItem.textTitle.setText(mMusic.getTitle());
        mBinding.layoutMusicInfoItem.textArtist.setText(mMusic.getArtist());
    }

    public interface MusicInfoBottomSheetCallback {

        void onAddToPlaylistClick(Music music);

        void onViewAlbumClick(Music music);

        void onViewArtistClick(Music music);

    }

}
