package com.oscarliang.spotifyclone.feature.musicinfo;

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
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.musicinfo.databinding.BottomSheetMusicInfoBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String MUSIC_ID_KEY = "musicId";

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<BottomSheetMusicInfoBinding> binding;
    private MusicInfoViewModel viewModel;
    private String musicId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public MusicInfoBottomSheet() {
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
        BottomSheetMusicInfoBinding viewBinding = BottomSheetMusicInfoBinding
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
        viewModel = new ViewModelProvider(this, factory).get(MusicInfoViewModel.class);
        viewModel.setMusicId(musicId);
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
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_music_info)))
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

                            // Init the music info
                            Glide.with(getContext())
                                    .load(result.data.getImageUrl())
                                    .placeholder(R.drawable.ic_music)
                                    .error(R.drawable.ic_error)
                                    .into(binding.get().layoutMusicInfoItem.imageMusic);
                            binding.get().layoutMusicInfoItem.textTitle.setText(result.data.getTitle());
                            binding.get().layoutMusicInfoItem.textArtist.setText(result.data.getArtist());

                            // Init button action
                            binding.get().btnAddToPlaylist.setOnClickListener(view ->
                                    navigatePlaylistSelectFragment(result.data.getId())
                            );
                            binding.get().btnViewAlbum.setOnClickListener(view ->
                                    navigateAlbumFragment(result.data.getAlbumId())
                            );
                            binding.get().btnViewArtist.setOnClickListener(view ->
                                    navigateArtistFragment(result.data.getArtistId())
                            );
                            break;
                    }
                })
        );
    }

    private void navigatePlaylistSelectFragment(String musicId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://playlistSelectFragment/" + musicId));
    }

    private void navigateAlbumFragment(String albumId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://albumFragment/" + albumId));
    }

    private void navigateArtistFragment(String artistId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://artistFragment/" + artistId));
    }

}