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
import com.oscarliang.spotifyclone.util.Resource;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

public class MusicFragment extends Fragment implements Injectable,
        MusicInfoBottomSheet.MusicInfoBottomSheetCallback,
        AddToPlaylistBottomSheet.AddToPlaylistBottomSheetCallback,
        CreatePlaylistWithMusicDialog.onCreatePlaylistWithMusicClickListener {

    private FragmentMusicBinding mBinding;
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
        mBinding = FragmentMusicBinding.inflate(inflater, container, false);
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
        mBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    if (mBinding.seekbar.getProgress() != currentTime) {
                        mBinding.seekbar.setProgress((int) currentTime);
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
                        if (vibrantSwatch != null) {
                            int startColor = vibrantSwatch.getRgb();
                            int endColor = ResourcesCompat.getColor(getResources(), R.color.black, null);
                            int[] colors = {startColor, endColor, endColor};
                            GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                            getView().setBackground(gradient);
                        }
                        return false;
                    }
                })
                .into(mBinding.imageMusic);
        mBinding.textTitle.setText(music.getTitle());
        mBinding.textArtist.setText(music.getArtist());
        mBinding.btnBack.setOnClickListener(view -> NavHostFragment.findNavController(this).navigateUp());
        mBinding.btnMore.setOnClickListener(view -> showMusicInfoBottomSheet(music));
        mBinding.btnPlay.setOnClickListener(view -> mMainViewModel.toggleMusic());
        mBinding.btnSkipNext.setOnClickListener(view -> mMainViewModel.skipNextMusic());
        mBinding.btnSkipPrevious.setOnClickListener(view -> mMainViewModel.skipPreviousMusic());
        mBinding.btnShuffle.setOnClickListener(view -> setShuffleMode(!mMainViewModel.getShuffleModeEnabled()));
        mBinding.btnRepeat.setOnClickListener(view -> setRepeatMode(mMainViewModel.getRepeatMode() + 1));
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
            mBinding.textDuration.setText(dateFormat.format(duration));
            mBinding.seekbar.setMax(duration.intValue());
        });
        mMainViewModel.getPlaying().observe(this, isPlaying ->
                mBinding.btnPlay.setBackgroundResource(isPlaying ? R.drawable.ic_pause_circle : R.drawable.ic_play_circle));
        mMainViewModel.getAddToPlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    Snackbar.make(getView(), "Added to " + resource.mData.getName(), Snackbar.LENGTH_LONG)
                            .setAction("VIEW", view -> navigatePlaylistFragment(resource.mData))
                            .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_green, null))
                            .show();
                    break;
                case ERROR:
                    Snackbar.make(getView(), "Error added to " + resource.mData.getName(),
                            Snackbar.LENGTH_LONG).show();
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
                    Snackbar.make(getView(), "Create playlist " + resource.mData.getName(), Snackbar.LENGTH_LONG)
                            .setAction("VIEW", view -> navigatePlaylistFragment(resource.mData))
                            .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_green, null))
                            .show();
                    break;
                case ERROR:
                    Snackbar.make(getView(), "Error create playlist " + resource.mData.getName(),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Ignore
                    break;
            }
        });
    }

    private void setCurrentTime(long position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        mBinding.textCurrentTime.setText(dateFormat.format(position));
    }

    private void setShuffleMode(boolean shuffleEnable) {
        mMainViewModel.setShuffleModeEnabled(shuffleEnable);
        mBinding.btnShuffle.setBackgroundResource(shuffleEnable ? R.drawable.ic_shuffle_on : R.drawable.ic_shuffle_off);
    }

    private void setRepeatMode(int repeatMode) {
        if (repeatMode > 2) {
            repeatMode = 0;
        }
        mMainViewModel.setRepeatMode(repeatMode);
        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF:
                mBinding.btnRepeat.setBackgroundResource(R.drawable.ic_repeat_off);
                break;
            case Player.REPEAT_MODE_ONE:
                mBinding.btnRepeat.setBackgroundResource(R.drawable.ic_repeat_one);
                break;
            case Player.REPEAT_MODE_ALL:
                mBinding.btnRepeat.setBackgroundResource(R.drawable.ic_repeat_on);
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
