package com.oscarliang.spotifyclone.feature.playlistedit;

import static java.util.Collections.singletonList;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.action.Action;
import com.oscarliang.spotifyclone.core.ui.action.ActionController;
import com.oscarliang.spotifyclone.core.ui.adapter.MusicEditAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.core.ui.util.MusicSorter;
import com.oscarliang.spotifyclone.feature.playlistedit.databinding.FragmentPlaylistEditBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistEditFragment extends Fragment {

    private static final String PLAYLIST_ID_KEY = "playlistId";

    @Inject
    ActionController actionController;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentPlaylistEditBinding> binding;
    private MusicEditAdapter musicEditAdapter;
    private PlaylistEditViewModel viewModel;
    private String playlistId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public PlaylistEditFragment() {
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
        FragmentPlaylistEditBinding viewBinding = FragmentPlaylistEditBinding
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
        viewModel = new ViewModelProvider(this, factory).get(PlaylistEditViewModel.class);
        viewModel.setPlaylistId(playlistId);

        initButton();
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
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_playlist_edit)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initButton() {
        binding.get().btnCancel.setOnClickListener(view ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void initRecyclerView() {
        musicEditAdapter = new MusicEditAdapter();
        binding.get().recyclerViewMusic.setAdapter(musicEditAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT
                ) {
                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target
                    ) {
                        int fromPosition = viewHolder.getAdapterPosition();
                        int toPosition = target.getAdapterPosition();
                        musicEditAdapter.swapMusics(fromPosition, toPosition);
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction
                    ) {
                        int position = viewHolder.getAdapterPosition();
                        Music music = musicEditAdapter.removeMusic(position);
                        String msg = getString(R.string.delete) + " " + music.getTitle();
                        Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, view -> musicEditAdapter.addMusic(music, position))
                                .show();
                    }
                });
        helper.attachToRecyclerView(binding.get().recyclerViewMusic);
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
                            binding.get().editPlaylistName.setText(result.data.first.getName());
                            // Sort the musics since the firestore query don't remain order
                            List<Music> sorted = MusicSorter.sort(
                                    result.data.second,
                                    result.data.first.getMusicIds()
                            );
                            musicEditAdapter.submitList(sorted);
                            binding.get().btnSave.setOnClickListener(view -> savePlaylist(result.data.first));
                            break;
                        case ERROR:
                            binding.get().layoutLoading.progressbar.setVisibility(View.GONE);
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

    private void savePlaylist(Playlist playlist) {
        // Get the updated playlist name from edit text
        String updatedName = binding.get().editPlaylistName.getText().toString().trim();
        // Check is updated name valid
        if (updatedName.isEmpty()) {
            String msg = getString(R.string.invalid_name);
            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
            return;
        }

        // Get the updates playlist musics from current list
        List<Music> updatedMusics = musicEditAdapter.getCurrentList();
        List<String> musicIds = getMusicIds(updatedMusics);

        // Check is musics has been updated
        if (Objects.equals(updatedName, playlist.getName())
                && Objects.equals(musicIds, playlist.getMusicIds())) {
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        disposables.add(viewModel.updatePlaylist(playlist, updatedName, musicIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // We always navigate to playlist after editing
                            navigatePlaylistFragment(playlistId);
                            actionController.sendAction(
                                    new Action(
                                            Action.Type.SHOW_SNACK_BAR,
                                            getString(R.string.playlist_update)
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

    private List<String> getMusicIds(List<Music> musics) {
        List<String> musicIds = new ArrayList<>();
        for (Music music : musics) {
            musicIds.add(music.getId());
        }
        return musicIds;
    }

    @SuppressLint("DiscouragedApi")
    private void navigatePlaylistFragment(String playlistId) {
        // Pop up all the backstack to library to
        // prevent navigating back to playlistEdit
        // Possible path:
        // Path 1 (Click playlist): Library -> Playlist -> PlaylistEdit -> Playlist
        // Path 2 (Click bottom sheet): Library -> PlaylistEdit -> Playlist
        int destinationId = getResources().getIdentifier(
                "libraryFragment",
                "id",
                getContext().getPackageName()
        );
        NavHostFragment
                .findNavController(this)
                .navigate(
                        Uri.parse("android-app://playlistFragment/" + playlistId),
                        new NavOptions.Builder()
                                .setPopUpTo(destinationId, false)
                                .build()
                );
    }

}