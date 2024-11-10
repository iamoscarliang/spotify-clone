package com.oscarliang.spotifyclone.feature.category;

import static java.util.Collections.singletonList;

import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.adapter.MusicAdapter;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.core.ui.util.LoadMoreState;
import com.oscarliang.spotifyclone.feature.category.databinding.FragmentCategoryBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoryFragment extends Fragment {

    private static final String CATEGORY_ID_KEY = "categoryId";

    @Inject
    MusicPlayer musicPlayer;

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentCategoryBinding> binding;
    private MusicAdapter musicAdapter;
    private CategoryViewModel viewModel;
    private String categoryId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(CATEGORY_ID_KEY)) {
            categoryId = args.getString(CATEGORY_ID_KEY);
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
        FragmentCategoryBinding viewBinding = FragmentCategoryBinding
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
        viewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);
        viewModel.setQuery(new CategoryQuery(categoryId, 20));

        initToolbar();
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
        subscribeLoadMoreState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_category)))
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

    private void initRecyclerView() {
        musicAdapter = new MusicAdapter(
                music -> {
                    musicPlayer.addMusic(music);
                    musicPlayer.toggleMusic();
                },
                music -> navigateMusicInfoBottomSheet(music.getId())
        );
        binding.get().recyclerViewMusic.setAdapter(musicAdapter);
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
                            binding.get().layoutLoading.shimmerLayout.showShimmer(true);
                            break;
                        case SUCCESS:
                            binding.get().layoutLoading.getRoot().setVisibility(View.GONE);
                            Glide.with(getContext())
                                    .load(result.data.first.getImageUrl())
                                    .error(R.drawable.ic_error)
                                    .into(binding.get().imageCategory);
                            String color = result.data.first.getColor();
                            if (color != null && !color.isEmpty()) {
                                binding.get().imageCategoryBg.setBackgroundColor(Color.parseColor(color));
                            }
                            binding.get().collapsingToolbar.setTitle(result.data.first.getName());
                            binding.get().textCategoryName.setText(result.data.first.getName());
                            musicAdapter.submitList(result.data.second);
                            break;
                        case ERROR:
                            binding.get().layoutLoading.shimmerLayout.hideShimmer();
                            Snackbar snackbar = Snackbar.make(
                                    binding.get().getRoot(),
                                    result.message,
                                    Snackbar.LENGTH_INDEFINITE
                            );
                            snackbar.setAction(R.string.retry, view -> {
                                        snackbar.dismiss();
                                        viewModel.retry();
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
                    binding.get().layoutLoadingMore.progressbarMore.setVisibility(
                            state == LoadMoreState.RUNNING ? View.VISIBLE : View.GONE
                    );
                    binding.get().layoutLoadingMore.textMessage.setVisibility(
                            state == LoadMoreState.NO_MORE ? View.VISIBLE : View.GONE
                    );
                })
        );
    }

    private void navigateMusicInfoBottomSheet(String musicId) {
        NavHostFragment
                .findNavController(this)
                .navigate(Uri.parse("android-app://musicInfoBottomSheet/" + musicId));
    }

}