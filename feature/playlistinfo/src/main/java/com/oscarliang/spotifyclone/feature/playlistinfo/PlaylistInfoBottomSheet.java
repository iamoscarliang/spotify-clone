package com.oscarliang.spotifyclone.feature.playlistinfo;

import static java.util.Collections.singletonList;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.action.Action;
import com.oscarliang.spotifyclone.core.ui.action.ActionController;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.playlistinfo.databinding.BottomSheetPlaylistInfoBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String PLAYLIST_ID_KEY = "playlistId";

    @Inject
    ActionController actionController;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<BottomSheetPlaylistInfoBinding> binding;
    private PlaylistInfoViewModel viewModel;
    private String playlistId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public PlaylistInfoBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        BottomSheetPlaylistInfoBinding viewBinding = BottomSheetPlaylistInfoBinding
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
        viewModel = new ViewModelProvider(this, factory).get(PlaylistInfoViewModel.class);
        viewModel.setPlaylistId(playlistId);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = ((BottomSheetDialog) getDialog());
        if (dialog != null) {
            dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        subscribeObserver();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_playlist_info)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
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

                            // Init the playlist info
                            Glide.with(getContext())
                                    .load(result.data.getImageUrl())
                                    .placeholder(R.drawable.ic_music)
                                    .error(R.drawable.ic_music)
                                    .into(binding.get().layoutPlaylistInfoItem.imagePlaylist);
                            binding.get().layoutPlaylistInfoItem.textPlaylist.setText(result.data.getName());
                            int count = result.data.getMusicIds() != null ? result.data.getMusicIds().size() : 0;
                            binding.get().layoutPlaylistInfoItem.textMusicCount.setText(String.valueOf(count));

                            // Init button action
                            binding.get().btnEditPlaylist.setOnClickListener(view ->
                                    navigatePlaylistEditFragment(result.data.getId())
                            );
                            binding.get().btnDeletePlaylist.setOnClickListener(view -> {
                                // Dismiss and show the delete dialog in previous fragment
                                dismiss();
                                actionController.sendAction(
                                        new Action(
                                                Action.Type.SHOW_DELETE_PLAYLIST_DIALOG,
                                                result.data.getId()
                                        )
                                );
                            });
                            break;
                    }
                })
        );
    }

    private void navigatePlaylistEditFragment(String playlistId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://playlistEditFragment/" + playlistId));
    }

}