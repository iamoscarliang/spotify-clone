package com.oscarliang.spotifyclone.feature.player;

import static java.util.Collections.singletonList;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.core.ui.util.DrawableUtils;
import com.oscarliang.spotifyclone.core.ui.util.PaletteLoader;
import com.oscarliang.spotifyclone.core.ui.util.ResourceUtils;
import com.oscarliang.spotifyclone.feature.player.databinding.FragmentPlayerBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlayerFragment extends Fragment {

    @Inject
    MusicPlayer musicPlayer;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentPlayerBinding> binding;
    private PlayerViewModel viewModel;
    private boolean isTrackingSeekBar = false;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.US);

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        FragmentPlayerBinding viewBinding = FragmentPlayerBinding
                .inflate(inflater, container, false);
        binding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, factory).get(PlayerViewModel.class);

        initButton();
        initSeekBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
        subscribePlaybackState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_player)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initButton() {
        binding.get().btnBack.setOnClickListener(view -> NavHostFragment.findNavController(this).navigateUp());
        binding.get().btnPlay.setOnClickListener(view -> musicPlayer.toggleMusic());
        binding.get().btnSkipNext.setOnClickListener(view -> musicPlayer.skipNextMusic());
        binding.get().btnSkipPrevious.setOnClickListener(view -> musicPlayer.skipPreviousMusic());
        binding.get().btnShuffle.setOnClickListener(view -> musicPlayer.toggleShuffleModeEnabled());
        binding.get().btnRepeat.setOnClickListener(view -> musicPlayer.toggleRepeatMode());
    }

    private void initSeekBar() {
        binding.get().seekbar.setPadding(0, 0, 0, 0);
        binding.get().seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.get().textCurrentTime.setText(timeFormat.format(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayer.seekTo(seekBar.getProgress());
                isTrackingSeekBar = false;
            }
        });
    }

    private void subscribeObserver() {
        disposables.add(viewModel.getResult()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    switch (result.state) {
                        case LOADING:
                            binding.get().layoutLoading.getRoot().setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            binding.get().layoutLoading.getRoot().setVisibility(View.GONE);

                            // Init the music info
                            Glide.with(getContext())
                                    .load(result.data.getImageUrl())
                                    .placeholder(R.drawable.ic_music)
                                    .error(R.drawable.ic_music)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(
                                                @Nullable GlideException e,
                                                @Nullable Object model,
                                                @NonNull Target<Drawable> target,
                                                boolean isFirstResource
                                        ) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(
                                                @NonNull Drawable resource,
                                                @NonNull Object model,
                                                Target<Drawable> target,
                                                @NonNull DataSource dataSource,
                                                boolean isFirstResource
                                        ) {
                                            // Create custom theme from music image
                                            int color = PaletteLoader.loadColor(DrawableUtils.toBitmap(resource));
                                            binding.get().getRoot().setBackgroundColor(color);
                                            return false;
                                        }
                                    })
                                    .into(binding.get().imageMusic);
                            binding.get().textTitle.setText(result.data.getTitle());
                            binding.get().textArtist.setText(result.data.getArtist());
                            binding.get().btnMore.setOnClickListener(view ->
                                    navigateMusicInfoBottomSheet(result.data.getId())
                            );
                            break;
                    }
                })
        );
    }

    private void subscribePlaybackState() {
        disposables.add(musicPlayer.getPlaybackState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playbackState -> {
                    if (!Objects.equals(playbackState.musicId, MusicPlayer.EMPTY_MUSIC)) {
                        viewModel.setMusicId(playbackState.musicId);
                    }
                    binding.get().textDuration.setText(timeFormat.format(playbackState.duration));
                    binding.get().seekbar.setMax((int) playbackState.duration);
                    binding.get().btnPlay
                            .setIconResource(ResourceUtils.getFillPlayResId(playbackState.isPlaying));
                    binding.get().btnShuffle
                            .setIconResource(ResourceUtils.getShuffleModeResId(playbackState.isShuffleModeEnabled));
                    binding.get().btnRepeat
                            .setIconResource(ResourceUtils.getRepeatModeResId(playbackState.repeatMode));
                })
        );
        disposables.add(Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    if (!isTrackingSeekBar) {
                        binding.get().seekbar.setProgress((int) musicPlayer.getCurrentTime());
                    }
                })
        );
    }

    private void navigateMusicInfoBottomSheet(String musicId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://musicInfoBottomSheet/" + musicId));
    }

}