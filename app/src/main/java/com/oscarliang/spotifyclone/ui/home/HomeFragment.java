package com.oscarliang.spotifyclone.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentHomeBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.util.Constants;

import java.util.Collections;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements Injectable {

    private FragmentHomeBinding mBinding;
    private LatestAlbumAdapter mLatestAlbumAdapter;
    private AllAlbumAdapter mAllAlbumAdapter;
    private AllArtistAdapter mAllArtistAdapter;
    private HomeViewModel mViewModel;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
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
        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(HomeViewModel.class);

        // Handle the back button event
        getActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // We make sure not navigate back to login page
                getActivity().finish();
            }
        });

        initToolbar();
        initDrawer();
        initSwipeRefreshLayout();
        initLatestAlbumRecyclerView();
        initAllAlbumRecyclerView();
        initAllArtistRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            loadAlbumsAndArtists();
        }
    }

    private void initToolbar() {
        mBinding.toolbar.setNavigationOnClickListener(view -> mBinding.drawerLayout.open());
    }

    private void initDrawer() {
        mBinding.drawer.navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                mAuth.signOut();
                navigateOnboardFragment();
            }
            item.setChecked(true);
            mBinding.drawerLayout.close();
            return true;
        });
        TextView textUser = mBinding.drawer.navView.getHeaderView(0).findViewById(R.id.text_user);
        textUser.setText(mAuth.getCurrentUser().getDisplayName());
    }

    private void initSwipeRefreshLayout() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            mBinding.swipeRefreshLayout.setRefreshing(false);
            loadAlbumsAndArtists();
        });
    }

    private void initLatestAlbumRecyclerView() {
        mLatestAlbumAdapter = new LatestAlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        mBinding.recyclerViewLatestAlbum.setAdapter(mLatestAlbumAdapter);
        mBinding.recyclerViewLatestAlbum.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.columns_count)));
    }

    private void initAllAlbumRecyclerView() {
        mAllAlbumAdapter = new AllAlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        mBinding.recyclerViewAllAlbum.setAdapter(mAllAlbumAdapter);
        mBinding.recyclerViewAllAlbum.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    private void initAllArtistRecyclerView() {
        mAllArtistAdapter = new AllArtistAdapter(artist -> navigateArtistFragment(artist.getId()));
        mBinding.recyclerViewAllArtist.setAdapter(mAllArtistAdapter);
        mBinding.recyclerViewAllArtist.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadAlbumsAndArtists() {
        mViewModel.setLatestAlbums(4);
        mViewModel.setAllAlbums(Constants.PAGINATION_COUNT);
        mViewModel.setAllArtists(Constants.PAGINATION_COUNT);
    }

    private void subscribeObservers() {
        mViewModel.getLatestAlbums().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mLatestAlbumAdapter.submitList(resource.mData);
                    mBinding.shimmerLayoutLatestAlbum.stopShimmer();
                    mBinding.shimmerLayoutLatestAlbum.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mLatestAlbumAdapter.submitList(Collections.emptyList());
                    mBinding.shimmerLayoutLatestAlbum.startShimmer();
                    mBinding.shimmerLayoutLatestAlbum.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getAllAlbums().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mAllAlbumAdapter.submitList(resource.mData);
                    mBinding.shimmerLayoutAllAlbum.stopShimmer();
                    mBinding.shimmerLayoutAllAlbum.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mAllAlbumAdapter.submitList(Collections.emptyList());
                    mBinding.shimmerLayoutAllAlbum.startShimmer();
                    mBinding.shimmerLayoutAllAlbum.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getAllArtists().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mAllArtistAdapter.submitList(resource.mData);
                    mBinding.shimmerLayoutAllArtist.stopShimmer();
                    mBinding.shimmerLayoutAllArtist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mAllArtistAdapter.submitList(Collections.emptyList());
                    mBinding.shimmerLayoutAllArtist.startShimmer();
                    mBinding.shimmerLayoutAllArtist.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void navigateOnboardFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_onboardFragment);
    }

    private void navigateAlbumFragment(String albumId) {
        Bundle bundle = new Bundle();
        bundle.putString("album", albumId);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_albumFragment, bundle);
    }

    private void navigateArtistFragment(String artist) {
        Bundle bundle = new Bundle();
        bundle.putString("artist", artist);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_artistFragment, bundle);
    }

}
