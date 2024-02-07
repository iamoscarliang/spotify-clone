package com.oscarliang.spotifyclone.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.oscarliang.spotifyclone.util.AutoClearedValue;
import com.oscarliang.spotifyclone.util.Constants;

import java.util.Collections;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements Injectable {

    private AutoClearedValue<FragmentHomeBinding> mBinding;
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
        FragmentHomeBinding viewBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(HomeViewModel.class);

        initToolbar();
        initDrawer();
        initSwipeRefreshLayout();
        initLatestAlbumRecyclerView();
        initAllAlbumRecyclerView();
        initAllArtistRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setLatest(4);
            mViewModel.setAll(Constants.PAGINATION_COUNT);
        }
    }

    private void initToolbar() {
        mBinding.get().toolbar.setNavigationOnClickListener(view -> mBinding.get().drawerLayout.open());
    }

    private void initDrawer() {
        mBinding.get().drawer.navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                mAuth.signOut();
                navigateOnboardFragment();
            }
            item.setChecked(true);
            mBinding.get().drawerLayout.close();
            return true;
        });
        TextView textUser = mBinding.get().drawer.navView.getHeaderView(0).findViewById(R.id.text_user);
        textUser.setText(mAuth.getCurrentUser().getDisplayName());
    }

    private void initSwipeRefreshLayout() {
        mBinding.get().swipeRefreshLayout.setOnRefreshListener(() -> {
            mBinding.get().swipeRefreshLayout.setRefreshing(false);
            mViewModel.refresh();
        });
    }

    private void initLatestAlbumRecyclerView() {
        mLatestAlbumAdapter = new LatestAlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        mBinding.get().recyclerViewLatestAlbum.setAdapter(mLatestAlbumAdapter);
        mBinding.get().recyclerViewLatestAlbum.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.columns_count)));
    }

    private void initAllAlbumRecyclerView() {
        mAllAlbumAdapter = new AllAlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        mBinding.get().recyclerViewAllAlbum.setAdapter(mAllAlbumAdapter);
        mBinding.get().recyclerViewAllAlbum.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    private void initAllArtistRecyclerView() {
        mAllArtistAdapter = new AllArtistAdapter(artist -> navigateArtistFragment(artist.getId()));
        mBinding.get().recyclerViewAllArtist.setAdapter(mAllArtistAdapter);
        mBinding.get().recyclerViewAllArtist.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    private void subscribeObservers() {
        mViewModel.getLatestAlbums().observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.mState) {
                case SUCCESS:
                    mLatestAlbumAdapter.submitList(listResource.mData);
                    mBinding.get().shimmerLayoutLatestAlbum.stopShimmer();
                    mBinding.get().shimmerLayoutLatestAlbum.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(mBinding.get().layoutContent, listResource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mLatestAlbumAdapter.submitList(Collections.emptyList());
                    mBinding.get().shimmerLayoutLatestAlbum.startShimmer();
                    mBinding.get().shimmerLayoutLatestAlbum.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getAllAlbums().observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.mState) {
                case SUCCESS:
                    mAllAlbumAdapter.submitList(listResource.mData);
                    mBinding.get().shimmerLayoutAllAlbum.stopShimmer();
                    mBinding.get().shimmerLayoutAllAlbum.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(mBinding.get().layoutContent, listResource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mAllAlbumAdapter.submitList(Collections.emptyList());
                    mBinding.get().shimmerLayoutAllAlbum.startShimmer();
                    mBinding.get().shimmerLayoutAllAlbum.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getAllArtists().observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.mState) {
                case SUCCESS:
                    mAllArtistAdapter.submitList(listResource.mData);
                    mBinding.get().shimmerLayoutAllArtist.stopShimmer();
                    mBinding.get().shimmerLayoutAllArtist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(mBinding.get().layoutContent, listResource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mAllArtistAdapter.submitList(Collections.emptyList());
                    mBinding.get().shimmerLayoutAllArtist.startShimmer();
                    mBinding.get().shimmerLayoutAllArtist.setVisibility(View.VISIBLE);
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
