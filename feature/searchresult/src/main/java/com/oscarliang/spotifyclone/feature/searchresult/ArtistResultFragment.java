package com.oscarliang.spotifyclone.feature.searchresult;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.adapter.ArtistAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.core.ui.util.LoadMoreState;
import com.oscarliang.spotifyclone.feature.searchresult.databinding.FragmentArtistResultBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArtistResultFragment extends Fragment {

    private static final String QUERY_KEY = "query";

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentArtistResultBinding> binding;
    private ArtistAdapter artistAdapter;
    private ArtistResultViewModel viewModel;
    private String query;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public ArtistResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(QUERY_KEY)) {
            query = args.getString(QUERY_KEY);
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
        FragmentArtistResultBinding viewBinding = FragmentArtistResultBinding
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
        viewModel = new ViewModelProvider(this, factory).get(ArtistResultViewModel.class);
        viewModel.setQuery(new SearchResultQuery(query, 20));

        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
        subscribeLoadMoreState();
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initRecyclerView() {
        artistAdapter = new ArtistAdapter(artist -> navigateArtistFragment(artist.getId()));
        binding.get().recyclerViewSearchResult.setAdapter(artistAdapter);
        binding.get().nestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    // Check is scroll to bottom
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        viewModel.loadNextPage();
                    }
                }
        );
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
                            artistAdapter.submitList(result.data);
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

    private void subscribeLoadMoreState() {
        disposables.add(viewModel.getLoadMoreState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state -> {
                    binding.get().layoutLoadingSearchMore.progressbarMore.setVisibility(
                            state == LoadMoreState.RUNNING ? View.VISIBLE : View.GONE
                    );
                    binding.get().layoutLoadingSearchMore.textMessage.setVisibility(
                            state == LoadMoreState.NO_MORE ? View.VISIBLE : View.GONE
                    );
                })
        );
    }

    private void navigateArtistFragment(String artistId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://artistFragment/" + artistId));
    }

}