package com.oscarliang.spotifyclone.feature.searchresult;

import static java.util.Collections.singletonList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.adapter.RecentSearchAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.searchresult.databinding.FragmentSearchResultBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchResultFragment extends Fragment {

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentSearchResultBinding> binding;
    private RecentSearchAdapter recentSearchAdapter;
    private SearchResultViewModel viewModel;

    @VisibleForTesting
    SearchResultFragmentFactory fragmentFactory = new SearchResultFragmentFactory();

    private final CompositeDisposable disposables = new CompositeDisposable();

    public SearchResultFragment() {
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
        FragmentSearchResultBinding viewBinding = FragmentSearchResultBinding
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
        viewModel = new ViewModelProvider(this, factory).get(SearchResultViewModel.class);

        initToolbar();
        initSearchView();
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
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_search_result)))
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

    private void initSearchView() {
        binding.get().searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    viewModel.setQuery(query);
                    viewModel.onSearchTriggered(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Resume the empty query type
                // when user delete all query text
                if (newText == null || newText.isEmpty()) {
                    viewModel.setQuery("");
                }
                return false;
            }
        });
    }

    private void initRecyclerView() {
        recentSearchAdapter = new RecentSearchAdapter(recentSearch ->
                binding.get().searchView.setQuery(recentSearch.getQuery(), true)
        );
        binding.get().layoutRecentSearch.recyclerViewRecentSearch.setAdapter(recentSearchAdapter);
        binding.get().layoutRecentSearch.btnClearSearchResult.setOnClickListener(view ->
                viewModel.clearRecentSearches()
        );
    }

    private void subscribeObserver() {
        disposables.add(viewModel.getQuery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    if (query == null || query.isEmpty()) {
                        onEmptyQuery();
                    } else {
                        onSearchQuery(query);
                    }
                })
        );
        disposables.add(viewModel.getRecentSearches(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recentSearches -> {
                    recentSearchAdapter.submitList(recentSearches);
                    if (recentSearches.isEmpty()) {
                        binding.get().layoutRecentSearch.btnClearSearchResult.setVisibility(View.GONE);
                    } else {
                        binding.get().layoutRecentSearch.btnClearSearchResult.setVisibility(View.VISIBLE);
                    }
                })
        );
    }

    private void onSearchQuery(String query) {
        hideSoftKeyBoard();
        binding.get().layoutRecentSearch.getRoot().setVisibility(View.GONE);
        binding.get().tabLayout.setVisibility(View.VISIBLE);
        binding.get().pager.setVisibility(View.VISIBLE);

        // Set up the pager and attach the fragment to it
        binding.get().pager.setUserInputEnabled(false);
        binding.get().pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                Fragment fragment = fragmentFactory.createFragment(position);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getItemCount() {
                return fragmentFactory.getFragmentCount();
            }
        });
        new TabLayoutMediator(
                binding.get().tabLayout, binding.get().pager,
                true,
                false,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(getString(R.string.label_music));
                            break;
                        case 1:
                            tab.setText(getString(R.string.label_album));
                            break;
                        case 2:
                            tab.setText(getString(R.string.label_artist));
                            break;
                    }
                }
        ).attach();
    }

    private void onEmptyQuery() {
        showSoftKeyBoard();
        binding.get().layoutRecentSearch.getRoot().setVisibility(View.VISIBLE);
        binding.get().tabLayout.setVisibility(View.GONE);
        binding.get().pager.setVisibility(View.GONE);
    }

    private void showSoftKeyBoard() {
        binding.get().searchView.requestFocus();
        WindowCompat
                .getInsetsController(getActivity().getWindow(), binding.get().searchView)
                .show(WindowInsetsCompat.Type.ime());
    }

    private void hideSoftKeyBoard() {
        binding.get().searchView.clearFocus();
        WindowCompat
                .getInsetsController(getActivity().getWindow(), binding.get().searchView)
                .hide(WindowInsetsCompat.Type.ime());
    }

}