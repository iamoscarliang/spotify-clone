package com.oscarliang.spotifyclone.ui.searchresult.artist;

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
import com.oscarliang.spotifyclone.databinding.FragmentSearchResultArtistBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.ui.common.adapter.ArtistAdapter;
import com.oscarliang.spotifyclone.util.AutoClearedValue;
import com.oscarliang.spotifyclone.util.Constants;
import com.oscarliang.spotifyclone.util.NextPageHandler;

import java.util.Collections;

import javax.inject.Inject;

public class ArtistSearchResultFragment extends Fragment implements Injectable {

    private static final String QUERY_KEY = "query";

    private String mQuery;

    private AutoClearedValue<FragmentSearchResultArtistBinding> mBinding;
    private ArtistAdapter mAdapter;
    private ArtistSearchResultViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mFactory;

    public ArtistSearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(QUERY_KEY)) {
            mQuery = args.getString(QUERY_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSearchResultArtistBinding viewBinding = FragmentSearchResultArtistBinding.inflate(inflater,
                container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this, mFactory).get(ArtistSearchResultViewModel.class);

        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setQuery(mQuery, Constants.PAGINATION_COUNT);
        }
    }

    private void initRecyclerView() {
        mAdapter = new ArtistAdapter(artist -> navigateArtistFragment(artist.getId()));
        mBinding.get().recyclerViewSearchResult.setAdapter(mAdapter);
        mBinding.get().recyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mBinding.get().nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    // Check is scroll to bottom
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        mViewModel.loadNextPage();
                    }
                });
    }

    private void subscribeObservers() {
        mViewModel.getArtists().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    new NextPageHandler() {
                        @Override
                        protected boolean isFirstPage() {
                            return mAdapter.getItemCount() == 0;
                        }

                        @Override
                        protected void loadFirstPage() {
                            mBinding.get().shimmerLayoutSearchResult.stopShimmer();
                            mBinding.get().shimmerLayoutSearchResult.setVisibility(View.GONE);
                            mBinding.get().progressbar.setVisibility(View.VISIBLE);
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
                            mBinding.get().progressbar.setVisibility(View.INVISIBLE);
                            mBinding.get().nestedScrollView
                                    .setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
                        }
                    }.loadPage();
                    break;
                case ERROR:
                    // Clear all result, so the loading state will be triggered
                    mAdapter.submitList(Collections.emptyList());
                    Snackbar.make(mBinding.get().layoutContent, resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Show the shimmer effect only when loading the first page
                    if (mAdapter.getItemCount() == 0) {
                        mBinding.get().shimmerLayoutSearchResult.startShimmer();
                        mBinding.get().shimmerLayoutSearchResult.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        });
    }

    private void navigateArtistFragment(String artistId) {
        Bundle bundle = new Bundle();
        bundle.putString("artist", artistId);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_artistFragment, bundle);
    }

}
