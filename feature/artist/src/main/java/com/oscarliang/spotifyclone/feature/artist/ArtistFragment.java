package com.oscarliang.spotifyclone.feature.artist;

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

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.model.Artist;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.adapter.AlbumAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.artist.databinding.FragmentArtistBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArtistFragment extends Fragment {

    private static final String ARTIST_ID_KEY = "artistId";

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentArtistBinding> binding;
    private AlbumAdapter albumAdapter;
    private ArtistViewModel viewModel;
    private String artistId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARTIST_ID_KEY)) {
            artistId = getArguments().getString(ARTIST_ID_KEY);
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
        FragmentArtistBinding viewBinding = FragmentArtistBinding
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
        viewModel = new ViewModelProvider(this, factory).get(ArtistViewModel.class);
        viewModel.setArtistId(artistId);

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
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_artist)))
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
        albumAdapter = new AlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        binding.get().recyclerViewAlbum.setAdapter(albumAdapter);
    }

    private void initArtist(Artist artist) {
        Glide.with(getContext())
                .load(artist.getImageUrl())
                .error(R.drawable.ic_error)
                .into(binding.get().imageArtistBg);
        Glide.with(getContext())
                .load(artist.getImageUrl())
                .error(R.drawable.ic_error)
                .into(binding.get().imageArtist);
        binding.get().textArtistName.setText(artist.getName());
        binding.get().collapsingToolbar.setTitle(artist.getName());
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
                            initArtist(result.data.first);
                            albumAdapter.submitList(result.data.second);
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

    private void navigateAlbumFragment(String albumId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://albumFragment/" + albumId));
    }

}