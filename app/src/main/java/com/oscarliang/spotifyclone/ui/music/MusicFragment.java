package com.oscarliang.spotifyclone.ui.music;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.Player;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentMusicBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.AddToPlaylistBottomSheet;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.MusicInfoBottomSheet;
import com.oscarliang.spotifyclone.ui.common.dialog.CreatePlaylistWithMusicDialog;
import com.oscarliang.spotifyclone.util.AutoClearedValue;
import com.oscarliang.spotifyclone.util.Resource;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class MusicFragment extends Fragment implements Injectable,
        MusicInfoBottomSheet.MusicInfoBottomSheetCallback,
        AddToPlaylistBottomSheet.AddToPlaylistBottomSheetCallback,
        CreatePlaylistWithMusicDialog.onCreatePlaylistWithMusicClickListener {

    private AutoClearedValue<FragmentMusicBinding> mBinding;
    private MainViewModel mMainViewModel;
    private boolean mUpdateSeekbar = true;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public MusicFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentMusicBinding viewBinding = FragmentMusicBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel = new ViewModelProvider(getActivity(), mFactory).get(MainViewModel.class);

        initSeekBar();
        subscribeObservers();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUpdateSeekbar = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUpdateSeekbar = true;
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

    private void initSeekBar() {
        mBinding.get().seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setCurrentTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateSeekbar = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMainViewModel.seekTo(seekBar.getProgress());
                mUpdateSeekbar = true;
            }
        });

        Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mUpdateSeekbar) {
                    long currentTime = mMainViewModel.getCurrentTime();
                    if (mBinding.get().seekbar.getProgress() != currentTime) {
                        mBinding.get().seekbar.setProgress((int) currentTime);
                        setCurrentTime(currentTime);
                    }
                }
                mainThreadHandler.postDelayed(this, 100);
            }
        });
    }

    private void initMusic(Music music) {
        Glide.with(getContext())
                .load(music.getImageUrl())
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
                        int[] colors = {startColor, endColor, endColor};
                        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                        mBinding.get().layoutContent.setBackground(gradient);
                        return false;
                    }
                })
                .into(mBinding.get().imageMusic);
        mBinding.get().textTitle.setText(music.getTitle());
        mBinding.get().textArtist.setText(music.getArtist());
        mBinding.get().btnBack.setOnClickListener(view -> NavHostFragment.findNavController(this).navigateUp());
        mBinding.get().btnMore.setOnClickListener(view -> showMusicInfoBottomSheet(music));
        mBinding.get().btnPlay.setOnClickListener(view -> mMainViewModel.toggleMusic());
        mBinding.get().btnSkipNext.setOnClickListener(view -> mMainViewModel.skipNextMusic());
        mBinding.get().btnSkipPrevious.setOnClickListener(view -> mMainViewModel.skipPreviousMusic());
        mBinding.get().btnShuffle.setOnClickListener(view -> setShuffleMode(!mMainViewModel.getShuffleModeEnabled()));
        mBinding.get().btnRepeat.setOnClickListener(view -> setRepeatMode(mMainViewModel.getRepeatMode() + 1));
        setShuffleMode(mMainViewModel.getShuffleModeEnabled());
        setRepeatMode(mMainViewModel.getRepeatMode());
    }

    private void subscribeObservers() {
        mMainViewModel.getCurrentMusic().observe(getViewLifecycleOwner(), currentMusic -> {
            if (currentMusic != null) {
                initMusic(currentMusic);
            }
        });
        mMainViewModel.getDuration().observe(getViewLifecycleOwner(), duration -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
            mBinding.get().textDuration.setText(dateFormat.format(duration));
            mBinding.get().seekbar.setMax(duration.intValue());
        });
        mMainViewModel.getPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            int resId = isPlaying ? R.drawable.ic_pause_circle : R.drawable.ic_play_circle;
            mBinding.get().btnPlay.setBackgroundResource(resId);
            mBinding.get().btnPlay.setTag(resId);
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
    }

    private void setCurrentTime(long position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        mBinding.get().textCurrentTime.setText(dateFormat.format(position));
    }

    private void setShuffleMode(boolean shuffleEnable) {
        mMainViewModel.setShuffleModeEnabled(shuffleEnable);
        mBinding.get().btnShuffle
                .setBackgroundResource(shuffleEnable ? R.drawable.ic_shuffle_on : R.drawable.ic_shuffle_off);
    }

    private void setRepeatMode(int repeatMode) {
        if (repeatMode > 2) {
            repeatMode = 0;
        }
        mMainViewModel.setRepeatMode(repeatMode);
        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF:
                mBinding.get().btnRepeat.setBackgroundResource(R.drawable.ic_repeat_off);
                break;
            case Player.REPEAT_MODE_ONE:
                mBinding.get().btnRepeat.setBackgroundResource(R.drawable.ic_repeat_one);
                break;
            case Player.REPEAT_MODE_ALL:
                mBinding.get().btnRepeat.setBackgroundResource(R.drawable.ic_repeat_on);
                break;
        }
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

}
