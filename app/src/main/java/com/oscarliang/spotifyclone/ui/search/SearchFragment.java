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

import javax.inject.Inject;

public class SearchFragment extends Fragment implements Injectable {

    private FragmentSearchBinding mBinding;
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
        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
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
        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(SearchViewModel.class);

        initToolbar();
        initDrawer();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setLoad(true);
        }
    }

    private void initToolbar() {
        mBinding.toolbar.setNavigationOnClickListener(view -> mBinding.drawerLayout.open());
        mBinding.imageSearchField.setOnClickListener(v -> navigateSearchResultFragment());
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

    private void initRecyclerView() {
        mAdapter = new CategoryAdapter(category -> navigateCategoryFragment(category.getName()));
        mBinding.recyclerViewCategory.setAdapter(mAdapter);
        mBinding.recyclerViewCategory.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.columns_count)));
    }

    private void subscribeObservers() {
        mViewModel.getCategories().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mAdapter.submitList(resource.mData);
                    mBinding.shimmerLayoutCategory.stopShimmer();
                    mBinding.shimmerLayoutCategory.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mBinding.shimmerLayoutCategory.startShimmer();
                    mBinding.shimmerLayoutCategory.setVisibility(View.VISIBLE);
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
