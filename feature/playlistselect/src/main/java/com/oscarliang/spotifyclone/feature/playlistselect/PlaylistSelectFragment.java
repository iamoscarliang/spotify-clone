package com.oscarliang.spotifyclone.feature.playlistselect;

import static java.util.Collections.singletonList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.auth.AuthManager;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.adapter.PlaylistSelectAdapter;
import com.oscarliang.spotifyclone.core.ui.dialog.CreatePlaylistDialog;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.playlistselect.databinding.FragmentPlaylistSelectBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistSelectFragment extends Fragment implements
        CreatePlaylistDialog.onCreatePlaylistClickListener {

    private static final String MUSIC_ID_KEY = "musicId";

    @Inject
    AuthManager authManager;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    @VisibleForTesting
    String musicId;

    private AutoClearedValue<FragmentPlaylistSelectBinding> binding;
    private PlaylistSelectAdapter playlistAdapter;
    private PlaylistSelectViewModel viewModel;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public PlaylistSelectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(MUSIC_ID_KEY)) {
            musicId = args.getString(MUSIC_ID_KEY);
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
        FragmentPlaylistSelectBinding viewBinding = FragmentPlaylistSelectBinding
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
        viewModel = new ViewModelProvider(this, factory).get(PlaylistSelectViewModel.class);
        viewModel.setUserId(authManager.getUserId());

        initToolbar();
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_playlist_select)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    @Override
    public void onCreatePlaylistClick(String playlistName) {
        disposables.add(viewModel.createPlaylist(playlistName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            String msg = getString(R.string.playlist_create);
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
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
        binding.get().btnCreate.setOnClickListener(view -> showCreatePlaylistDialog());
    }

    private void initRecyclerView() {
        playlistAdapter = new PlaylistSelectAdapter(playlist -> addMusicToPlaylist(playlist));
        binding.get().recyclerViewPlaylist.setAdapter(playlistAdapter);
    }

    private void subscribeObserver() {
        disposables.add(viewModel.getResult()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    switch (result.state) {
                        case LOADING:
                            binding.get().layoutLoading.getRoot().setVisibility(View.VISIBLE);
                            binding.get().layoutLoading.progressbar.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            binding.get().layoutLoading.getRoot().setVisibility(View.GONE);
                            playlistAdapter.submitList(result.data);
                            break;
                        case ERROR:
                            binding.get().layoutLoading.progressbar.setVisibility(View.GONE);
                            Snackbar snackbar = Snackbar.make(
                                    binding.get().getRoot(),
                                    result.message,
                                    Snackbar.LENGTH_INDEFINITE
                            );
                            snackbar.setAction(R.string.retry, view -> {
                                        snackbar.dismiss();
                                        viewModel.retry();
                                    })
                                    .show();
                            break;
                    }
                })
        );
    }

    private void showCreatePlaylistDialog() {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        dialog.show(getChildFragmentManager(), null);
    }

    private void addMusicToPlaylist(Playlist playlist) {
        // Check is music already in playlist
        if (playlist.getMusicIds().contains(musicId)) {
            String msg = getString(R.string.music_already_exist);
            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
            return;
        }

        disposables.add(viewModel.addMusicToPlaylist(playlist, musicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> NavHostFragment.findNavController(this).navigateUp(),
                        throwable -> {
                            String msg = throwable.getMessage();
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
                        }
                )
        );
    }

}