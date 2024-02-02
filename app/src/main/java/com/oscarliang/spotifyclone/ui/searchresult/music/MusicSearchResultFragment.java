package com.oscarliang.spotifyclone.ui.searchresult.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentSearchResultMusicBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.ui.common.adapter.MusicAdapter;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.AddToPlaylistBottomSheet;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.MusicInfoBottomSheet;
import com.oscarliang.spotifyclone.ui.common.dialog.CreatePlaylistWithMusicDialog;
import com.oscarliang.spotifyclone.util.Constants;
import com.oscarliang.spotifyclone.util.NextPageHandler;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class MusicSearchResultFragment extends Fragment implements Injectable,
        MusicInfoBottomSheet.MusicInfoBottomSheetCallback,
        AddToPlaylistBottomSheet.AddToPlaylistBottomSheetCallback,
        CreatePlaylistWithMusicDialog.onCreatePlaylistWithMusicClickListener {

    private static final String QUERY = "query";

    private String mQuery;

    private FragmentSearchResultMusicBinding mBinding;
    private MusicAdapter mAdapter;
    private MusicSearchResultViewModel mSearchResultViewModel;
    private MainViewModel mMainViewModel;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public MusicSearchResultFragment() {
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
        mBinding = FragmentSearchResultMusicBinding.inflate(inflater, container, false);
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
        mSearchResultViewModel = new ViewModelProvider(this, mFactory).get(MusicSearchResultViewModel.class);
        mMainViewModel = new ViewModelProvider(getActivity(), mFactory).get(MainViewModel.class);

        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mSearchResultViewModel.setQuery(mQuery, Constants.PAGINATION_COUNT);
        }
    }

    @Override
    public void onAddToPlaylistClick(Music music) {
        showAddToPlaylistBottomSheet(music);
    }

    @Override
    public void onViewAlbumClick(Music music) {
        navigateAlbumFragment(music);
    }

    @Override
    public void onViewArtistClick(Music music) {
        navigateArtistFragment(music);
    }

    @Override
    public void onPlaylistClick(Playlist playlist, Music music) {
        mMainViewModel.addToPlaylist(mAuth.getCurrentUser().getUid(), playlist, music);
    }

    @Override
    public void onCreatePlaylistClick(Music music) {
        showCreatePlaylistWithMusicDialog(music);
    }

    @Override
    public void onCreatePlaylistWithMusicClick(String playlistName, Music music) {
        mMainViewModel.addToNewPlaylist(mAuth.getCurrentUser().getUid(), playlistName, music);
    }

    private void initRecyclerView() {
        mAdapter = new MusicAdapter(music -> {
            mMainViewModel.addMusic(music);
            mMainViewModel.toggleMusic();
        }, music -> showMusicInfoBottomSheet(music));
        mBinding.recyclerViewSearchResult.setAdapter(mAdapter);
        mBinding.recyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mBinding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    // Check is scroll to bottom
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        mSearchResultViewModel.loadNextPage();
                    }
                });
    }

    private void subscribeObservers() {
        mSearchResultViewModel.getMusics().observe(getViewLifecycleOwner(), resource -> {
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
        mMainViewModel.getAddToPlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    Snackbar.make(getView(), "Added to " + resource.mData.getName(), Snackbar.LENGTH_LONG)
                            .setAction("VIEW", view -> navigatePlaylistFragment(resource.mData))
                            .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_green, null))
                            .show();
                    break;
                case ERROR:
                    Snackbar.make(getView(), "Error added to " + resource.mData.getName(),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Ignore
                    break;
            }
        });
        mMainViewModel.getAddToNewPlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    Snackbar.make(getView(), "Create playlist " + resource.mData.getName(), Snackbar.LENGTH_LONG)
                            .setAction("VIEW", view -> navigatePlaylistFragment(resource.mData))
                            .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_green, null))
                            .show();
                    break;
                case ERROR:
                    Snackbar.make(getView(), "Error create playlist " + resource.mData.getName(),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Ignore
                    break;
            }
        });
    }

    private void showMusicInfoBottomSheet(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        MusicInfoBottomSheet bottomSheet = new MusicInfoBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), null);
    }

    private void showAddToPlaylistBottomSheet(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        AddToPlaylistBottomSheet bottomSheet = new AddToPlaylistBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), null);
    }

    private void showCreatePlaylistWithMusicDialog(Music music) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        CreatePlaylistWithMusicDialog dialog = new CreatePlaylistWithMusicDialog();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), null);
    }

    private void navigateAlbumFragment(Music music) {
        Bundle bundle = new Bundle();
        bundle.putString("album", music.getAlbumId());
        NavController navController = NavHostFragment.findNavController(getParentFragment());
        navController.navigate(R.id.action_to_albumFragment, bundle);
    }

    private void navigateArtistFragment(Music music) {
        Bundle bundle = new Bundle();
        bundle.putString("artist", music.getArtistId());
        NavController navController = NavHostFragment.findNavController(getParentFragment());
        navController.navigate(R.id.action_to_artistFragment, bundle);
    }

    private void navigatePlaylistFragment(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        NavController navController = NavHostFragment.findNavController(getParentFragment());
        navController.navigate(R.id.action_to_playlistFragment, bundle);
    }

}
