package com.oscarliang.spotifyclone.ui.artist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentArtistBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.ui.common.adapter.AlbumAdapter;

import javax.inject.Inject;

public class ArtistFragment extends Fragment implements Injectable {

    private static final String ARTIST = "artist";

    private String mArtistId;

    private FragmentArtistBinding mBinding;
    private AlbumAdapter mAdapter;
    private ArtistViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mFactory;

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArtistId = getArguments().getString(ARTIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentArtistBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this, mFactory).get(ArtistViewModel.class);

        initToolbar();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setArtistId(mArtistId);
        }
    }

    private void initToolbar() {
        mBinding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void initRecyclerView() {
        mAdapter = new AlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        mBinding.recyclerViewAlbum.setAdapter(mAdapter);
        mBinding.recyclerViewAlbum.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void subscribeObservers() {
        mViewModel.getArtist().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    Glide.with(getContext())
                            .load(resource.mData.getImageUrl())
                            .placeholder(R.drawable.ic_artist)
                            .error(R.drawable.ic_artist)
                            .into(mBinding.imageArtist);
                    mBinding.toolbar.setTitle(resource.mData.getName());
                    mBinding.textBrowse.setVisibility(View.VISIBLE);
                    mBinding.shimmerLayoutArtist.stopShimmer();
                    mBinding.shimmerLayoutArtist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mBinding.textBrowse.setVisibility(View.INVISIBLE);
                    mBinding.shimmerLayoutArtist.startShimmer();
                    mBinding.shimmerLayoutArtist.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getAlbums().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mAdapter.submitList(resource.mData);
                    mBinding.shimmerLayoutAlbum.stopShimmer();
                    mBinding.shimmerLayoutAlbum.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mBinding.shimmerLayoutAlbum.startShimmer();
                    mBinding.shimmerLayoutAlbum.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void navigateAlbumFragment(String albumId) {
        Bundle bundle = new Bundle();
        bundle.putString("album", albumId);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_albumFragment, bundle);
    }

}
