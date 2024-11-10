package com.oscarliang.spotifyclone.feature.library;

import static java.util.Collections.singletonList;

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

import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.auth.AuthManager;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.action.Action;
import com.oscarliang.spotifyclone.core.ui.action.ActionController;
import com.oscarliang.spotifyclone.core.ui.adapter.PlaylistAdapter;
import com.oscarliang.spotifyclone.core.ui.dialog.CreatePlaylistDialog;
import com.oscarliang.spotifyclone.core.ui.dialog.DeletePlaylistDialog;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.library.databinding.FragmentLibraryBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LibraryFragment extends Fragment implements
        CreatePlaylistDialog.onCreatePlaylistClickListener,
        DeletePlaylistDialog.OnDeletePlaylistClickListener {

    @Inject
    AuthManager authManager;

    @Inject
    ActionController actionController;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentLibraryBinding> binding;
    private PlaylistAdapter playlistAdapter;
    private LibraryViewModel viewModel;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public LibraryFragment() {
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
        FragmentLibraryBinding viewBinding = FragmentLibraryBinding
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
        viewModel = new ViewModelProvider(getActivity(), factory).get(LibraryViewModel.class);
        viewModel.setUserId(authManager.getUserId());

        initButton();
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
        subscribeActionState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_library)))
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

    @Override
    public void onDeletePlaylistClick(String playlistId) {
        disposables.add(viewModel.deletePlaylist(playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            String msg = getString(R.string.playlist_delete);
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
                        },
                        throwable -> {
                            String msg = throwable.getMessage();
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
                        }
                )
        );
    }

    private void initButton() {
        binding.get().btnCreate.setOnClickListener(view -> showCreatePlaylistDialog());
    }

    private void initRecyclerView() {
        playlistAdapter = new PlaylistAdapter(
                playlist -> navigatePlaylistFragment(playlist.getId()),
                playlist -> navigatePlaylistInfoBottomSheet(playlist.getId())
        );
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

    private void subscribeActionState() {
        disposables.add(actionController.getAction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    Action action = event.getContentIfNotHandled();
                    if (action == null) {
                        return;
                    }
                    switch (action.type) {
                        case SHOW_DELETE_PLAYLIST_DIALOG:
                            showDeletePlaylistDialog(action.message);
                            break;
                        case SHOW_SNACK_BAR:
                            Snackbar.make(binding.get().getRoot(), action.message, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                })
        );
    }

    private void showCreatePlaylistDialog() {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        dialog.show(getChildFragmentManager(), null);
    }

    private void showDeletePlaylistDialog(String playlistId) {
        Bundle bundle = new Bundle();
        bundle.putString("playlistId", playlistId);
        DeletePlaylistDialog dialog = new DeletePlaylistDialog();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), null);
    }

    private void navigatePlaylistInfoBottomSheet(String playlistId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://playlistInfoBottomSheet/" + playlistId));
    }

    private void navigatePlaylistFragment(String playlistId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://playlistFragment/" + playlistId));
    }

}