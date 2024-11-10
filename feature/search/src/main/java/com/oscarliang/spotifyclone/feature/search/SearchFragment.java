package com.oscarliang.spotifyclone.feature.search;

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
import com.oscarliang.spotifyclone.core.ui.adapter.CategoryAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.search.databinding.FragmentSearchBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends Fragment {

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentSearchBinding> binding;
    private CategoryAdapter categoryAdapter;
    private SearchViewModel viewModel;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public SearchFragment() {
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
        FragmentSearchBinding viewBinding = FragmentSearchBinding
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
        viewModel = new ViewModelProvider(getActivity(), factory).get(SearchViewModel.class);

        initSearchbar();
        initButton();
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
        subscribeSortState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_search)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initSearchbar() {
        binding.get().imageSearchBar.setOnClickListener(view -> navigateSearchResultFragment());
    }

    private void initButton() {
        binding.get().btnSort.setOnClickListener(view -> viewModel.onToggleSort());
    }

    private void initRecyclerView() {
        categoryAdapter = new CategoryAdapter(category -> navigateCategoryFragment(category.getId()));
        binding.get().recyclerViewCategory.setAdapter(categoryAdapter);
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
                            categoryAdapter.submitList(result.data);
                            break;
                        case ERROR:
                            binding.get().layoutLoading.progressbar.setVisibility(View.GONE);
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

    private void subscribeSortState() {
        disposables.add(viewModel.getDirection()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(direction -> {
                    switch (direction) {
                        case ASCENDING:
                            binding.get().btnSort.setText(getString(R.string.sort_ascending));
                            break;
                        case DESCENDING:
                            binding.get().btnSort.setText(getString(R.string.sort_descending));
                            break;
                    }
                })
        );
    }

    private void navigateCategoryFragment(String categoryId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://categoryFragment/" + categoryId));
    }

    private void navigateSearchResultFragment() {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://searchResultFragment"));
    }

}