package com.oscarliang.spotifyclone.ui.searchresult.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentSearchResultAlbumBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.ui.common.adapter.AlbumAdapter;
import com.oscarliang.spotifyclone.util.Constants;
import com.oscarliang.spotifyclone.util.NextPageHandler;

import javax.inject.Inject;

public class AlbumSearchResultFragment extends Fragment implements Injectable {

    private static final String QUERY = "query";

    private String mQuery;

    private FragmentSearchResultAlbumBinding mBinding;
    private AlbumAdapter mAdapter;
    private AlbumSearchResultViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mFactory;

    public AlbumSearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuery = getArguments().getString(QUERY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentSearchResultAlbumBinding.inflate(inflater, container, false);
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
        mViewModel = new ViewModelProvider(this, mFactory).get(AlbumSearchResultViewModel.class);

        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setQuery(mQuery, Constants.PAGINATION_COUNT);
        }
    }

    private void initRecyclerView() {
        mAdapter = new AlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        mBinding.recyclerViewSearchResult.setAdapter(mAdapter);
        mBinding.recyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mBinding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    // Check is scroll to bottom
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        mViewModel.loadNextPage();
                    }
                });
    }

    private void subscribeObservers() {
        mViewModel.getAlbums().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    new NextPageHandler() {
                        @Override
                        protected boolean isFirstPage() {
                            return mAdapter.getItemCount() == 0;
                        }

                        @Override
                        protected void loadFirstPage() {
                            mBinding.shimmerLayoutSearchResult.stopShimmer();
                            mBinding.shimmerLayoutSearchResult.setVisibility(View.GONE);
                            mBinding.progressbar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected boolean hasMoreResult() {
                            return resource.mData.size() % Constants.PAGINATION_COUNT == 0
                                    && resource.mData.size() != mAdapter.getItemCount();
                        }

                        @Override
                        protected void loadResult() {
                            mAdapter.submitList(resource.mData);
                        }

                        @Override
                        protected void onQueryExhausted() {
                            mBinding.progressbar.setVisibility(View.INVISIBLE);
                            mBinding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
                        }
                    }.loadPage();
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Show the shimmer effect only when loading the first page
                    if (mAdapter.getItemCount() == 0) {
                        mBinding.shimmerLayoutSearchResult.startShimmer();
                        mBinding.shimmerLayoutSearchResult.setVisibility(View.VISIBLE);
                    }
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
