package com.oscarliang.spotifyclone.feature.playlist;

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
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.action.Action;
import com.oscarliang.spotifyclone.core.ui.action.ActionController;
import com.oscarliang.spotifyclone.core.ui.adapter.MusicAdapter;
import com.oscarliang.spotifyclone.core.ui.dialog.DeletePlaylistDialog;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.core.ui.util.DrawableUtils;
import com.oscarliang.spotifyclone.core.ui.util.MusicSorter;
import com.oscarliang.spotifyclone.core.ui.util.PaletteLoader;
import com.oscarliang.spotifyclone.core.ui.util.ResourceUtils;
import com.oscarliang.spotifyclone.feature.playlist.databinding.FragmentPlaylistBinding;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistFragment extends Fragment implements
        DeletePlaylistDialog.OnDeletePlaylistClickListener {

    private static final String PLAYLIST_ID_KEY = "playlistId";

    @Inject
    MusicPlayer musicPlayer;

    @Inject
    ActionController actionController;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentPlaylistBinding> binding;
    private MusicAdapter musicAdapter;
    private PlaylistViewModel viewModel;
    private String playlistId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(PLAYLIST_ID_KEY)) {
            playlistId = args.getString(PLAYLIST_ID_KEY);
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
        FragmentPlaylistBinding viewBinding = FragmentPlaylistBinding
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
        viewModel = new ViewModelProvider(this, factory).get(PlaylistViewModel.class);
        viewModel.setPlaylistId(playlistId);

        initToolbar();
        initButton();
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
        subscribeActionState();
        subscribePlaybackState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_playlist)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    @Override
    public void onDeletePlaylistClick(String playlistId) {
        disposables.add(viewModel.deletePlaylist(playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // Navigate back and show snackbar in previous fragment
                            NavHostFragment.findNavController(this).navigateUp();
                            actionController.sendAction(
                                    new Action(
                                            Action.Type.SHOW_SNACK_BAR,
                                            getString(R.string.playlist_delete)
                                    )
                            );
                        },
                        throwable -> {
                            String msg = throwable.getMessage();
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
                        }
                )
        );
    }

    private void initToolbar() {
        binding.get().toolbar.setNavigationOnClickListener(view ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void initButton() {
        binding.get().btnEditPlaylist.setOnClickListener(view -> navigatePlaylistEditFragment(playlistId));
        binding.get().btnDeletePlaylist.setOnClickListener(view -> showDeletePlaylistDialog());
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

    private void initPlaylist(Playlist playlist) {
        Glide.with(getContext())
                .load(playlist.getImageUrl())
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
                        // Create custom theme from playlist image
                        int color = PaletteLoader.loadColor(DrawableUtils.toBitmap(resource));
                        binding.get().imagePlaylistBg.setBackgroundColor(color);
                        return false;
                    }
                })
                .into(binding.get().imagePlaylist);
        binding.get().collapsingToolbar.setTitle(playlist.getName());
        binding.get().textPlaylistName.setText(playlist.getName());
        int count = playlist.getMusicIds() != null ? playlist.getMusicIds().size() : 0;
        binding.get().textPlaylistMusicCount.setText(String.valueOf(count));
    }

    private void initPlayFab(List<Music> musics) {
        // We only show the fab when music is available, since
        // the fab will has no effect when being click if no music
        binding.get().fabPlay.setVisibility(View.VISIBLE);
        binding.get().fabPlay.setOnClickListener(view -> {
            // Add all music to the player if not currently playing
            if (!Objects.equals(musicPlayer.getCurrentPlaylistId(), playlistId)) {
                musicPlayer.addPlaylist(musics, playlistId);
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
                            initPlaylist(result.data.first);
                            // Sort the musics since the firestore query don't remain order
                            List<Music> sorted = MusicSorter.sort(
                                    result.data.second,
                                    result.data.first.getMusicIds()
                            );
                            initPlayFab(sorted);
                            musicAdapter.submitList(sorted);
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

    private void subscribeActionState() {
        disposables.add(actionController.getAction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    Action action = event.getContentIfNotHandled();
                    if (action == null) {
                        return;
                    }
                    if (action.type == Action.Type.SHOW_SNACK_BAR) {
                        Snackbar.make(binding.get().getRoot(), action.message, Snackbar.LENGTH_LONG).show();
                    }
                })
        );
    }

    private void subscribePlaybackState() {
        disposables.add(musicPlayer.getPlaybackState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playbackState -> {
                    // Check is current playlistId equal user's playlistId
                    if (Objects.equals(musicPlayer.getCurrentPlaylistId(), playlistId)) {
                        binding.get().fabPlay
                                .setImageResource(ResourceUtils.getPlayResId(playbackState.isPlaying));
                    }
                })
        );
    }

    private void showDeletePlaylistDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("playlistId", playlistId);
        DeletePlaylistDialog dialog = new DeletePlaylistDialog();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), null);
    }

    private void navigateMusicInfoBottomSheet(String musicId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://musicInfoBottomSheet/" + musicId));
    }

    private void navigatePlaylistEditFragment(String playlistId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://playlistEditFragment/" + playlistId));
    }

}