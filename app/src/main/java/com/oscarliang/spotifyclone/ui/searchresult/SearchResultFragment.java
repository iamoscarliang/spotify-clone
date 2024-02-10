package com.oscarliang.spotifyclone.ui.searchresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentSearchResultBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.ui.searchresult.album.AlbumSearchResultFragment;
import com.oscarliang.spotifyclone.ui.searchresult.artist.ArtistSearchResultFragment;
import com.oscarliang.spotifyclone.ui.searchresult.music.MusicSearchResultFragment;
import com.oscarliang.spotifyclone.util.AutoClearedValue;

import javax.inject.Inject;

public class SearchResultFragment extends Fragment implements Injectable {

    private AutoClearedValue<FragmentSearchResultBinding> mBinding;
    private SearchResultViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mFactory;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSearchResultBinding viewBinding = FragmentSearchResultBinding.inflate(inflater,
                container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this, mFactory).get(SearchResultViewModel.class);

        initToolbar();
        initSearchView();
        subscribeObservers();

        if (mViewModel.isEmpty()) {
            showSoftKeyBoard();
        }
    }

    private void initToolbar() {
        mBinding.get().toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigateUp());
    }

    private void initSearchView() {
        mBinding.get().searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mBinding.get().searchView.clearFocus();
                mViewModel.setQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void subscribeObservers() {
        mViewModel.getQuery().observe(getViewLifecycleOwner(), query -> initPager(query));
    }

    private void initPager(String query) {
        mBinding.get().pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                Fragment fragment;
                switch (position) {
                    case 0:
                        fragment = new MusicSearchResultFragment();
                        break;
                    case 1:
                        fragment = new ArtistSearchResultFragment();
                        break;
                    case 2:
                        fragment = new AlbumSearchResultFragment();
                        break;
                    default:
                        throw new IllegalArgumentException("ViewPager Fragment not found!");
                }
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });
        new TabLayoutMediator(mBinding.get().tabLayout, mBinding.get().pager,
                true, false, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.music));
                    break;
                case 1:
                    tab.setText(getString(R.string.artist));
                    break;
                case 2:
                    tab.setText(getString(R.string.album));
                    break;
            }
        }).attach();
    }

    private void showSoftKeyBoard() {
        mBinding.get().searchView.post(() -> {
            mBinding.get().searchView.requestFocus();
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.get().searchView)
                    .show(WindowInsetsCompat.Type.ime());
        });
    }

}
