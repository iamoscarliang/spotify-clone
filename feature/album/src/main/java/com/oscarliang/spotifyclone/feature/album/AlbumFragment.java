package com.oscarliang.spotifyclone.feature.album;

import static java.util.Collections.singletonList;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.adapter.MusicAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.core.ui.util.DrawableUtils;
import com.oscarliang.spotifyclone.core.ui.util.PaletteLoader;
import com.oscarliang.spotifyclone.core.ui.util.ResourceUtils;
import com.oscarliang.spotifyclone.feature.album.databinding.FragmentAlbumBinding;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlbumFragment extends Fragment {

    private static final String ALBUM_ID_KEY = "albumId";

    @Inject
    MusicPlayer musicPlayer;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentAlbumBinding> binding;
    private MusicAdapter musicAdapter;
    private AlbumViewModel viewModel;
    private String albumId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ALBUM_ID_KEY)) {
            albumId = getArguments().getString(ALBUM_ID_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        FragmentAlbumBinding viewBinding = FragmentAlbumBinding
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
        viewModel = new ViewModelProvider(this, factory).get(AlbumViewModel.class);
        viewModel.setAlbumId(albumId);

        initToolbar();
        initRecyclerView();
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
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_album)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initToolbar() {
        binding.get().toolbar.setNavigationOnClickListener(view ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void initRecyclerView() {
        musicAdapter = new MusicAdapter(
                music -> {
                    musicPlayer.addMusic(music);
                    musicPlayer.toggleMusic();
                },
                music -> navigateMusicInfoBottomSheet(music.getId())
        );
        binding.get().recyclerViewMusic.setAdapter(musicAdapter);
    }

    private void initAlbum(Album album) {
        Glide.with(getContext())
                .load(album.getImageUrl())
                .error(R.drawable.ic_error)
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
                        // Create custom theme from album image
                        int color = PaletteLoader.loadColor(DrawableUtils.toBitmap(resource));
                        binding.get().imageAlbumBg.setBackgroundColor(color);
                        return false;
                    }
                })
                .into(binding.get().imageAlbum);
        binding.get().collapsingToolbar.setTitle(album.getTitle());
        binding.get().textAlbumTitle.setText(album.getTitle());
        binding.get().textAlbumArtist.setText(album.getArtist());
        binding.get().textAlbumYear.setText(album.getYear());
    }

    private void initPlayFab(List<Music> musics) {
        // We only show the fab when music is available, since
        // the fab will has no effect when being click if no music
        binding.get().fabPlay.setVisibility(View.VISIBLE);
        binding.get().fabPlay.setOnClickListener(view -> {
            // Add all music to the player if not currently playing
            if (!Objects.equals(musicPlayer.getCurrentPlaylistId(), albumId)) {
                musicPlayer.addPlaylist(musics, albumId);
            }
            musicPlayer.toggleMusic();
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
                            binding.get().layoutLoading.shimmerLayout.showShimmer(true);
                            break;
                        case SUCCESS:
                            binding.get().layoutLoading.getRoot().setVisibility(View.GONE);
                            initAlbum(result.data.first);
                            initPlayFab(result.data.second);
                            musicAdapter.submitList(result.data.second);
                            break;
                        case ERROR:
                            binding.get().layoutLoading.shimmerLayout.hideShimmer();
                            Snackbar snackbar = Snackbar.make(
                                    binding.get().getRoot(),
                                    result.message,
                                    Snackbar.LENGTH_INDEFINITE
                            );
                            snackbar.setAction(R.string.retry, view -> {
                                        viewModel.retry();
                                        snackbar.dismiss();
                                    })
                                    .show();
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
                    // Check is current playlist equal album's playlist
                    if (Objects.equals(musicPlayer.getCurrentPlaylistId(), albumId)) {
                        binding.get().fabPlay
                                .setImageResource(ResourceUtils.getPlayResId(playbackState.isPlaying));
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