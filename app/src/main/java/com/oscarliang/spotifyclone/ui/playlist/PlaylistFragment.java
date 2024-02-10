package com.oscarliang.spotifyclone.ui.playlist;

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
import com.oscarliang.spotifyclone.databinding.FragmentPlaylistBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.ui.common.adapter.MusicAdapter;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.AddToPlaylistBottomSheet;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.MusicInfoBottomSheet;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.PlaylistInfoBottomSheet;
import com.oscarliang.spotifyclone.ui.common.dialog.CreatePlaylistWithMusicDialog;
import com.oscarliang.spotifyclone.ui.common.dialog.DeletePlaylistDialog;
import com.oscarliang.spotifyclone.util.AutoClearedValue;
import com.oscarliang.spotifyclone.util.MusicSorter;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class PlaylistFragment extends Fragment implements Injectable,
        PlaylistInfoBottomSheet.PlaylistInfoBottomSheetCallback,
        DeletePlaylistDialog.OnConfirmDeletePlaylistClickListener,
        MusicInfoBottomSheet.MusicInfoBottomSheetCallback,
        AddToPlaylistBottomSheet.AddToPlaylistBottomSheetCallback,
        CreatePlaylistWithMusicDialog.onCreatePlaylistWithMusicClickListener {

    private static final String PLAYLIST_KEY = "playlist";

    @VisibleForTesting
    Playlist mPlaylist;

    private AutoClearedValue<FragmentPlaylistBinding> mBinding;
    private MusicAdapter mAdapter;
    private PlaylistViewModel mPlaylistViewModel;
    private MainViewModel mMainViewModel;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(PLAYLIST_KEY)) {
            mPlaylist = args.getParcelable(PLAYLIST_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentPlaylistBinding viewBinding = FragmentPlaylistBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlaylistViewModel = new ViewModelProvider(this, mFactory).get(PlaylistViewModel.class);
        mMainViewModel = new ViewModelProvider(getActivity(), mFactory).get(MainViewModel.class);

        initPlaylist();
        initToolbar();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mPlaylistViewModel.setPlaylistMusics(mPlaylist.getMusicIds());
        }
    }

    @Override
    public void onEditPlaylistClick(Playlist playlist) {
        navigatePlaylistEditFragment(playlist);
    }

    @Override
    public void onDeletePlaylistClick(Playlist playlist) {
        showDeletePlaylistDialog(playlist);
    }

    @Override
    public void onConfirmDeletePlaylistClick(Playlist playlist) {
        mPlaylistViewModel.deletePlaylist(mAuth.getCurrentUser().getUid(), playlist);
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

    private void initPlaylist() {
        Glide.with(getContext())
                .load(mPlaylist.getImageUrl())
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
                        if (vibrantSwatch != null) {
                            int startColor = vibrantSwatch.getRgb();
                            int endColor = ResourcesCompat.getColor(getResources(), R.color.black, null);
                            int[] colors = {startColor, endColor};
                            GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                            mBinding.get().imagePlaylistBg.setBackground(gradient);
                        }
                        return false;
                    }
                })
                .into(mBinding.get().imagePlaylist);
        mBinding.get().toolbar.setTitle(mPlaylist.getName());
        int musicCount = mPlaylist.getMusicIds() != null ? mPlaylist.getMusicIds().size() : 0;
        mBinding.get().textMusicCount.setText(getString(R.string.playlist_count, String.valueOf(musicCount)));
    }

    private void initToolbar() {
        mBinding.get().toolbar.setNavigationOnClickListener(view -> navigateUp());
        mBinding.get().toolbar.inflateMenu(R.menu.menu_playlist);
        mBinding.get().toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_more) {
                showPlaylistInfoBottomSheet(mPlaylist);
                return true;
            }
            return false;
        });
    }

    private void initRecyclerView() {
        mAdapter = new MusicAdapter(music -> {
            mMainViewModel.addMusic(music);
            mMainViewModel.toggleMusic();
        }, music -> showMusicInfoBottomSheet(music));
        mBinding.get().recyclerViewMusic.setAdapter(mAdapter);
        mBinding.get().recyclerViewMusic.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void subscribeObservers() {
        mPlaylistViewModel.getPlaylistMusics().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                // Handler the case when returning absent livedata from an empty playlist
                mBinding.get().fabPlay.setVisibility(View.INVISIBLE);
                mBinding.get().shimmerLayoutMusic.stopShimmer();
                mBinding.get().shimmerLayoutMusic.setVisibility(View.GONE);
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    List<Music> sortedMusics = MusicSorter.sort(resource.mData, mPlaylist.getMusicIds());
                    mAdapter.submitList(sortedMusics);
                    mBinding.get().fabPlay.setVisibility(View.VISIBLE);
                    mBinding.get().fabPlay.setOnClickListener(view -> {
                        if (!isPlayingCurrentPlaylist()) {
                            mMainViewModel.addPlaylist(sortedMusics, mPlaylist.getId());
                        }
                        mMainViewModel.toggleMusic();
                    });
                    mBinding.get().shimmerLayoutMusic.stopShimmer();
                    mBinding.get().shimmerLayoutMusic.setVisibility(View.GONE);
                    break;
                case ERROR:
                    mBinding.get().layoutLoadingStatePlaylist.layoutLoadingState.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStatePlaylist.textMessage.setText(resource.mMessage);
                    break;
                case LOADING:
                    mBinding.get().fabPlay.setVisibility(View.INVISIBLE);
                    mBinding.get().shimmerLayoutMusic.startShimmer();
                    mBinding.get().shimmerLayoutMusic.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStatePlaylist.layoutLoadingState.setVisibility(View.GONE);
                    break;
            }
        });
        mPlaylistViewModel.getDeletePlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    mBinding.get().progressbar.setVisibility(View.GONE);
                    navigateUp();
                    break;
                case ERROR:
                    mBinding.get().progressbar.setVisibility(View.GONE);
                    Snackbar.make(mBinding.get().layoutContent, resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mBinding.get().progressbar.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mMainViewModel.getPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            // Check is current playlist equal user's playlist
            if (isPlayingCurrentPlaylist()) {
                int resId = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
                mBinding.get().fabPlay.setImageResource(resId);
                mBinding.get().fabPlay.setTag(resId);   // Visible for testing
            }
        });
        mBinding.get().layoutLoadingStatePlaylist.btnRetry.setOnClickListener(view -> mPlaylistViewModel.retry());
    }

    private void showMusicInfoBottomSheet(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        MusicInfoBottomSheet bottomSheet = new MusicInfoBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), null);
    }

    private void showPlaylistInfoBottomSheet(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        PlaylistInfoBottomSheet bottomSheet = new PlaylistInfoBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), null);
    }

    private void showDeletePlaylistDialog(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        DeletePlaylistDialog dialog = new DeletePlaylistDialog();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), null);
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

    private void navigatePlaylistEditFragment(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_playlistFragment_to_playlistEditFragment, bundle);
    }

    private void navigateUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    private boolean isPlayingCurrentPlaylist() {
        return mMainViewModel.getPlaylistMetadata() != null
                && Objects.equals(mMainViewModel.getPlaylistMetadata().title, mPlaylist.getId());
    }

}
