package com.oscarliang.spotifyclone.feature.home;

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
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.adapter.AlbumCardAdapter;
import com.oscarliang.spotifyclone.core.ui.adapter.ArtistCardAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.home.databinding.FragmentHomeBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentHomeBinding> binding;
    private AlbumCardAdapter albumCardAdapter;
    private ArtistCardAdapter artistCardAdapter;
    private HomeViewModel viewModel;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public HomeFragment() {
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
        FragmentHomeBinding viewBinding = FragmentHomeBinding
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
        viewModel = new ViewModelProvider(getActivity(), factory).get(HomeViewModel.class);
        viewModel.setQuery(new HomeQuery(10, 10));

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
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_home)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initRecyclerView() {
        albumCardAdapter = new AlbumCardAdapter(album -> navigateAlbumFragment(album.getId()));
        binding.get().recyclerViewAllAlbum.setAdapter(albumCardAdapter);
        artistCardAdapter = new ArtistCardAdapter(artist -> navigateArtistFragment(artist.getId()));
        binding.get().recyclerViewAllArtist.setAdapter(artistCardAdapter);
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
                            albumCardAdapter.submitList(result.data.first);
                            artistCardAdapter.submitList(result.data.second);
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