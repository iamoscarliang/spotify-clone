package com.oscarliang.spotifyclone.ui.common.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.BottomSheetAddToPlaylistBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.common.adapter.PlaylistSelectAdapter;
import com.oscarliang.spotifyclone.ui.library.LibraryViewModel;
import com.oscarliang.spotifyclone.util.AutoClearedValue;

import java.util.Collections;

import javax.inject.Inject;

public class AddToPlaylistBottomSheet extends BottomSheetDialogFragment implements Injectable {

    private static final String MUSIC_KEY = "music";

    private Music mMusic;

    private AutoClearedValue<BottomSheetAddToPlaylistBinding> mBinding;
    private PlaylistSelectAdapter mAdapter;
    private LibraryViewModel mLibraryViewModel;
    private AddToPlaylistBottomSheetCallback mCallback;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public AddToPlaylistBottomSheet() {
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
            mCallback = (AddToPlaylistBottomSheetCallback) getParentFragment();
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
        BottomSheetAddToPlaylistBinding viewBinding = BottomSheetAddToPlaylistBinding.inflate(inflater,
                container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLibraryViewModel = new ViewModelProvider(getActivity(), mFactory).get(LibraryViewModel.class);
        mBinding.get().btnCreatePlaylist.setOnClickListener(v -> {
            // Create a new playlist, and add the current music into it
            mCallback.onCreatePlaylistClick(mMusic);
            dismiss();
        });

        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mLibraryViewModel.setUser(mAuth.getCurrentUser().getUid());
        }
    }

    private void initRecyclerView() {
        mAdapter = new PlaylistSelectAdapter(playlist -> {
            // Add the current music directly to the playlist
            mCallback.onPlaylistClick(playlist, mMusic);
            dismiss();
        });
        mBinding.get().recyclerViewPlaylist.setAdapter(mAdapter);
        mBinding.get().recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void subscribeObservers() {
        mLibraryViewModel.getPlaylists().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mAdapter.submitList(resource.mData);
                    mBinding.get().shimmerLayoutPlaylist.stopShimmer();
                    mBinding.get().shimmerLayoutPlaylist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "No network connection!", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.image_pager_bg)
                            .show();
                    break;
                case LOADING:
                    mAdapter.submitList(Collections.emptyList());
                    mBinding.get().shimmerLayoutPlaylist.startShimmer();
                    mBinding.get().shimmerLayoutPlaylist.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    public interface AddToPlaylistBottomSheetCallback {

        void onPlaylistClick(Playlist playlist, Music music);

        void onCreatePlaylistClick(Music music);

    }

}
