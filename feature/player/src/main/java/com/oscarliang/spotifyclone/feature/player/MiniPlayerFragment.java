package com.oscarliang.spotifyclone.feature.player;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.core.ui.util.ResourceUtils;
import com.oscarliang.spotifyclone.feature.player.databinding.FragmentMiniPlayerBinding;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MiniPlayerFragment extends Fragment {

    @Inject
    MusicPlayer musicPlayer;

    private AutoClearedValue<FragmentMiniPlayerBinding> binding;
    private MiniPlayerAdapter miniPlayerAdapter;
    private boolean isTrackingSeekBar = false;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public MiniPlayerFragment() {
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
        FragmentMiniPlayerBinding viewBinding = FragmentMiniPlayerBinding
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
        initButton();
        initPager();
        initSeekBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribePlaybackState();
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initButton() {
        binding.get().btnPlay.setOnClickListener(view -> musicPlayer.toggleMusic());
    }

    private void initPager() {
        miniPlayerAdapter = new MiniPlayerAdapter(() -> navigatePlayerFragment());
        binding.get().pager.setAdapter(miniPlayerAdapter);
    }

    private void initSeekBar() {
        binding.get().seekbar.setPadding(0, 0, 0, 0);
        binding.get().seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    private void subscribePlaybackState() {
        // Hide the player initially to prevent show and hide when startup
        binding.get().getRoot().setVisibility(View.GONE);
        disposables.add(musicPlayer.getPlaybackState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playbackState -> {
                    miniPlayerAdapter.submitList(playbackState.musics);
                    if (!Objects.equals(playbackState.musicId, MusicPlayer.EMPTY_MUSIC)) {
                        // Show the player and switch the pager to current music
                        binding.get().getRoot().setVisibility(View.VISIBLE);
                        switchPagerToCurrentMusic(musicPlayer.getCurrentIndex());
                    } else {
                        // Hide the player when playlist end
                        binding.get().getRoot().setVisibility(View.GONE);
                    }
                    binding.get().seekbar.setMax((int) playbackState.duration);
                    binding.get().btnPlay.setIconResource(ResourceUtils.getPlayResId(playbackState.isPlaying));
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

    private void switchPagerToCurrentMusic(int position) {
        if (position == -1) {
            return;
        }
        // Delay the initializing of the viewpager, since
        // it has no effect when layout is still inflating.
        // Also register the listener after updating the
        // position, or it may reset to 0 when startup.
        binding.get().pager.post(() -> {
            binding.get().pager.setCurrentItem(position, false);
            binding.get().pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    int current = musicPlayer.getCurrentIndex();
                    if (position > current) {
                        musicPlayer.skipNextMusic();
                    } else if (position < current) {
                        musicPlayer.skipPreviousMusic();
                    }
                }
            });
        });
    }

    private void navigatePlayerFragment() {
        NavHostFragment.findNavController(this).navigate(
                Uri.parse("android-app://playerFragment"),
                new NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_vertical)
                        .setPopExitAnim(R.anim.slide_out_vertical)
                        .build()
        );
    }

}