package com.oscarliang.spotifyclone.ui.album;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentAlbumBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.ui.common.adapter.MusicAdapter;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.AddToPlaylistBottomSheet;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.MusicInfoBottomSheet;
import com.oscarliang.spotifyclone.ui.common.dialog.CreatePlaylistWithMusicDialog;
import com.oscarliang.spotifyclone.util.AutoClearedValue;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class AlbumFragment extends Fragment implements Injectable,
        MusicInfoBottomSheet.MusicInfoBottomSheetCallback,
        AddToPlaylistBottomSheet.AddToPlaylistBottomSheetCallback,
        CreatePlaylistWithMusicDialog.onCreatePlaylistWithMusicClickListener {

    private static final String ALBUM_ID_KEY = "album";

    @VisibleForTesting
    String mAlbumId;

    private AutoClearedValue<FragmentAlbumBinding> mBinding;
    private MusicAdapter mAdapter;
    private AlbumViewModel mAlbumViewModel;
    private MainViewModel mMainViewModel;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ALBUM_ID_KEY)) {
            mAlbumId = getArguments().getString(ALBUM_ID_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentAlbumBinding viewBinding = FragmentAlbumBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAlbumViewModel = new ViewModelProvider(this, mFactory).get(AlbumViewModel.class);
        mMainViewModel = new ViewModelProvider(getActivity(), mFactory).get(MainViewModel.class);

        initToolbar();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mAlbumViewModel.setAlbumId(mAlbumId);
        }
    }

    @Override
    public void onAddToPlaylistClick(Music music) {
        showAddToPlaylistBottomSheet(music);
    }

    @Override
    public void onViewAlbumClick(Music music) {
        navigateAlbumFragment(music);
    }

    @Override
    public void onViewArtistClick(Music music) {
        navigateArtistFragment(music);
    }

    @Override
    public void onPlaylistClick(Playlist playlist, Music music) {
        mMainViewModel.addToPlaylist(mAuth.getCurrentUser().getUid(), playlist, music);
    }

    @Override
    public void onCreatePlaylistClick(Music music) {
        showCreatePlaylistWithMusicDialog(music);
    }

    @Override
    public void onCreatePlaylistWithMusicClick(String playlistName, Music music) {
        mMainViewModel.addToNewPlaylist(mAuth.getCurrentUser().getUid(), playlistName, music);
    }

    private void initToolbar() {
        mBinding.get().toolbar.setNavigationOnClickListener(view ->
                NavHostFragment.findNavController(this).navigateUp());
    }

    private void initRecyclerView() {
        mAdapter = new MusicAdapter(music -> {
            mMainViewModel.addMusic(music);
            mMainViewModel.toggleMusic();
        }, music -> showMusicInfoBottomSheet(music));
        mBinding.get().recyclerViewMusic.setAdapter(mAdapter);
        mBinding.get().recyclerViewMusic.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private void subscribeObservers() {
        mAlbumViewModel.getAlbum().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    Glide.with(getContext())
                            .load(resource.mData.getImageUrl())
                            .placeholder(R.drawable.ic_music)
                            .error(R.drawable.ic_music)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model,
                                                            @NonNull Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model,
                                                               Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                    // Create custom theme from album image
                                    Palette palette = Palette.from(((BitmapDrawable) resource).getBitmap()).generate();
                                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                                    if (vibrantSwatch == null) {
                                        // In case palette fail to find a proper color from bitmap
                                        // Manually find the color with the largest population from all swatch
                                        List<Palette.Swatch> vibrantSwatches = palette.getSwatches();
                                        Palette.Swatch maxSwatch = null;
                                        int maxPopulation = 0;
                                        for (Palette.Swatch swatch : vibrantSwatches) {
                                            if (swatch.getPopulation() > maxPopulation) {
                                                maxPopulation = swatch.getPopulation();
                                                maxSwatch = swatch;
                                            }
                                        }
                                        vibrantSwatch = maxSwatch;
                                    }
                                    int startColor = vibrantSwatch.getRgb();
                                    int endColor = ResourcesCompat.getColor(getResources(), R.color.black, null);
                                    int[] colors = {startColor, endColor};
                                    GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                                    mBinding.get().imageAlbumBg.setBackground(gradient);
                                    return false;
                                }
                            })
                            .into(mBinding.get().imageAlbum);
                    mBinding.get().collapsingToolbar.setTitle(resource.mData.getTitle());
                    mBinding.get().textAlbumArtist.setText(resource.mData.getArtist());
                    mBinding.get().textAlbumYear.setText(getResources().getString(R.string.album_year, resource.mData.getYear()));
                    mBinding.get().shimmerLayoutAlbum.stopShimmer();
                    mBinding.get().shimmerLayoutAlbum.setVisibility(View.GONE);
                    break;
                case ERROR:
                    mBinding.get().layoutLoadingStateAlbum.layoutLoadingState.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateAlbum.textMessage.setText(resource.mMessage);
                    break;
                case LOADING:
                    mBinding.get().imageAlbum.setImageResource(0);
                    mBinding.get().imageAlbumBg.setBackgroundResource(R.color.black);
                    mBinding.get().collapsingToolbar.setTitle("");
                    mBinding.get().textAlbumArtist.setText("");
                    mBinding.get().textAlbumYear.setText("");
                    mBinding.get().shimmerLayoutAlbum.startShimmer();
                    mBinding.get().shimmerLayoutAlbum.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateAlbum.layoutLoadingState.setVisibility(View.GONE);
                    break;
            }
        });
        mAlbumViewModel.getMusics().observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.mState) {
                case SUCCESS:
                    mAdapter.submitList(listResource.mData);
                    mBinding.get().fabPlay.setVisibility(View.VISIBLE);
                    mBinding.get().fabPlay.setOnClickListener(view -> {
                        if (!isPlayingCurrentAlbum()) {
                            mMainViewModel.addPlaylist(listResource.mData, mAlbumId);
                        }
                        mMainViewModel.toggleMusic();
                    });
                    mBinding.get().shimmerLayoutMusic.stopShimmer();
                    mBinding.get().shimmerLayoutMusic.setVisibility(View.GONE);
                    break;
                case ERROR:
                    mBinding.get().layoutLoadingStateAlbum.layoutLoadingState.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateAlbum.textMessage.setText(listResource.mMessage);
                    break;
                case LOADING:
                    mBinding.get().fabPlay.setVisibility(View.INVISIBLE);
                    mBinding.get().shimmerLayoutMusic.startShimmer();
                    mBinding.get().shimmerLayoutMusic.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateAlbum.layoutLoadingState.setVisibility(View.GONE);
                    break;
            }
        });
        mMainViewModel.getAddToPlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    String msg = getString(R.string.playlist_add, resource.mData.getName());
                    Snackbar.make(mBinding.get().layoutContent, msg, Snackbar.LENGTH_LONG)
                            .setAction("VIEW", view -> navigatePlaylistFragment(resource.mData))
                            .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_green, null))
                            .show();
                    break;
                case ERROR:
                    Snackbar.make(mBinding.get().layoutContent, resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Ignore
                    break;
            }
        });
        mMainViewModel.getAddToNewPlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    String msg = getString(R.string.playlist_add, resource.mData.getName());
                    Snackbar.make(mBinding.get().layoutContent, msg, Snackbar.LENGTH_LONG)
                            .setAction("VIEW", view -> navigatePlaylistFragment(resource.mData))
                            .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_green, null))
                            .show();
                    break;
                case ERROR:
                    Snackbar.make(mBinding.get().layoutContent, resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Ignore
                    break;
            }
        });
        mMainViewModel.getPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            // Check is current playlist equal album's playlist
            if (isPlayingCurrentAlbum()) {
                int resId = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
                mBinding.get().fabPlay.setImageResource(resId);
                mBinding.get().fabPlay.setTag(resId);   // Visible for testing
            }
        });
        mBinding.get().layoutLoadingStateAlbum.btnRetry.setOnClickListener(view -> mAlbumViewModel.retry());
    }

    private void showMusicInfoBottomSheet(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        MusicInfoBottomSheet bottomSheet = new MusicInfoBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), null);
    }

    private void showAddToPlaylistBottomSheet(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        AddToPlaylistBottomSheet bottomSheet = new AddToPlaylistBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), null);
    }

    private void showCreatePlaylistWithMusicDialog(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        CreatePlaylistWithMusicDialog dialog = new CreatePlaylistWithMusicDialog();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), null);
    }

    private void navigateAlbumFragment(Music music) {
        Bundle bundle = new Bundle();
        bundle.putString("album", music.getAlbumId());
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_albumFragment, bundle);
    }

    private void navigateArtistFragment(Music music) {
        Bundle bundle = new Bundle();
        bundle.putString("artist", music.getArtistId());
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_artistFragment, bundle);
    }

    private void navigatePlaylistFragment(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_playlistFragment, bundle);
    }

    private boolean isPlayingCurrentAlbum() {
        return mMainViewModel.getPlaylistMetadata() != null
                && Objects.equals(mMainViewModel.getPlaylistMetadata().title, mAlbumId);
    }

}
