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
import com.oscarliang.spotifyclone.util.AutoClearedValue;

public class MusicInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String MUSIC_KEY = "music";

    private Music mMusic;

    private AutoClearedValue<BottomSheetMusicInfoBinding> mBinding;
    private MusicInfoBottomSheetCallback mCallback;

    public MusicInfoBottomSheet() {
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
        BottomSheetMusicInfoBinding viewBinding = BottomSheetMusicInfoBinding.inflate(inflater,
                container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.get().btnAddToPlaylist.setOnClickListener(v -> {
            mCallback.onAddToPlaylistClick(mMusic);
            dismiss();
        });
        mBinding.get().btnViewAlbum.setOnClickListener(v -> {
            mCallback.onViewAlbumClick(mMusic);
            dismiss();
        });
        mBinding.get().btnViewArtist.setOnClickListener(v -> {
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
                .into(mBinding.get().layoutMusicInfoItem.imageMusic);
        mBinding.get().layoutMusicInfoItem.textTitle.setText(mMusic.getTitle());
        mBinding.get().layoutMusicInfoItem.textArtist.setText(mMusic.getArtist());
    }

    public interface MusicInfoBottomSheetCallback {

        void onAddToPlaylistClick(Music music);

        void onViewAlbumClick(Music music);

        void onViewArtistClick(Music music);

    }

}
