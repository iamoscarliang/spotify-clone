package com.oscarliang.spotifyclone.ui.search;

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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentSearchBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.util.AutoClearedValue;

import java.util.Collections;

import javax.inject.Inject;

public class SearchFragment extends Fragment implements Injectable {

    private AutoClearedValue<FragmentSearchBinding> mBinding;
    private CategoryAdapter mAdapter;
    private SearchViewModel mViewModel;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSearchBinding viewBinding = FragmentSearchBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(SearchViewModel.class);

        initToolbar();
        initDrawer();
        initSwipeRefreshLayout();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setLoad(true);
        }
    }

    private void initToolbar() {
        mBinding.get().toolbar.setNavigationOnClickListener(view -> mBinding.get().drawerLayout.open());
        mBinding.get().imageSearchField.setOnClickListener(view -> navigateSearchResultFragment());
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

    private void initRecyclerView() {
        mAdapter = new CategoryAdapter(category -> navigateCategoryFragment(category.getName()));
        mBinding.get().recyclerViewCategory.setAdapter(mAdapter);
        mBinding.get().recyclerViewCategory.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.columns_count)));
    }

    private void subscribeObservers() {
        mViewModel.getCategories().observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.mState) {
                case SUCCESS:
                    mAdapter.submitList(listResource.mData);
                    mBinding.get().shimmerLayoutCategory.stopShimmer();
                    mBinding.get().shimmerLayoutCategory.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(mBinding.get().layoutContent, listResource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mAdapter.submitList(Collections.emptyList());
                    mBinding.get().shimmerLayoutCategory.startShimmer();
                    mBinding.get().shimmerLayoutCategory.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void navigateOnboardFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_onboardFragment);
    }

    private void navigateCategoryFragment(String category) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
    }

    private void navigateSearchResultFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_searchFragment_to_searchResultFragment);
    }

}
