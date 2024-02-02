package com.oscarliang.spotifyclone.ui.library;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentLibraryBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.common.adapter.PlaylistAdapter;
import com.oscarliang.spotifyclone.ui.common.bottomsheet.PlaylistInfoBottomSheet;
import com.oscarliang.spotifyclone.ui.common.dialog.CreatePlaylistDialog;
import com.oscarliang.spotifyclone.ui.common.dialog.DeletePlaylistDialog;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.Collections;

import javax.inject.Inject;

public class LibraryFragment extends Fragment implements Injectable,
        PlaylistInfoBottomSheet.PlaylistInfoBottomSheetCallback,
        CreatePlaylistDialog.onCreatePlaylistClickListener,
        DeletePlaylistDialog.OnConfirmDeletePlaylistClickListener {

    private FragmentLibraryBinding mBinding;
    private PlaylistAdapter mAdapter;
    private LibraryViewModel mLibraryViewModel;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
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
        mLibraryViewModel = new ViewModelProvider(getActivity(), mFactory).get(LibraryViewModel.class);

        initToolbar();
        initDrawer();
        initSwipeRefreshLayout();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            loadPlaylists();
        }
    }

    @Override
    public void onEditPlaylistClick(Playlist playlist) {
        navigatePlaylistEditFragment(playlist);
    }

    @Override
    public void onDeletePlaylistClick(Playlist playlist) {
        showDeletePlaylistDialog(playlist);
    }

    @Override
    public void onCreatePlaylistClick(String playlistName) {
        mLibraryViewModel.addPlaylist(mAuth.getCurrentUser().getUid(), playlistName);
    }

    @Override
    public void onConfirmDeletePlaylistClick(Playlist playlist) {
        mLibraryViewModel.deletePlaylist(mAuth.getCurrentUser().getUid(), playlist);
    }

    private void initToolbar() {
        mBinding.toolbar.setNavigationOnClickListener(view -> mBinding.drawerLayout.open());
        mBinding.toolbar.inflateMenu(R.menu.menu_library);
        mBinding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add_playlist) {
                showCreatePlaylistDialog();
                return true;
            }
            return false;
        });
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

    private void initSwipeRefreshLayout() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            mBinding.swipeRefreshLayout.setRefreshing(false);
            loadPlaylists();
        });
    }

    private void initRecyclerView() {
        mAdapter = new PlaylistAdapter(
                playlist -> navigatePlaylistFragment(playlist),
                playlist -> showPlaylistInfoBottomSheet(playlist));
        mBinding.recyclerViewPlaylist.setAdapter(mAdapter);
        mBinding.recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void loadPlaylists() {
        mLibraryViewModel.setUser(mAuth.getCurrentUser().getUid());
    }

    private void subscribeObservers() {
        mLibraryViewModel.getPlaylists().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mAdapter.submitList(resource.mData);
                    mBinding.shimmerLayoutPlaylist.stopShimmer();
                    mBinding.shimmerLayoutPlaylist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mAdapter.submitList(Collections.emptyList());
                    mBinding.shimmerLayoutPlaylist.startShimmer();
                    mBinding.shimmerLayoutPlaylist.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mLibraryViewModel.getAddPlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource != null) {
                switch (resource.mState) {
                    case SUCCESS:
                        mAdapter.addToList(resource.mData);
                        Snackbar.make(getView(), "Create playlist " + resource.mData.getName(),
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case ERROR:
                        Snackbar.make(getView(), "Error creating playlist " + resource.mData.getName(),
                                        Snackbar.LENGTH_LONG).show();
                        break;
                    case LOADING:
                        // Ignore
                        break;
                }
            }
        });
        mLibraryViewModel.getDeletePlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource != null) {
                switch (resource.mState) {
                    case SUCCESS:
                        mAdapter.removeFromList(resource.mData);
                        Snackbar.make(getView(), "Delete playlist " + resource.mData.getName(),
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case ERROR:
                        Snackbar.make(getView(), "Error deleting playlist " + resource.mData.getName(),
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case LOADING:
                        // Ignore
                        break;
                }
            }
        });
    }

    private void showPlaylistInfoBottomSheet(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        PlaylistInfoBottomSheet bottomSheet = new PlaylistInfoBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), null);
    }

    private void showCreatePlaylistDialog() {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        dialog.show(getChildFragmentManager(), null);
    }

    private void showDeletePlaylistDialog(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        DeletePlaylistDialog dialog = new DeletePlaylistDialog();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), null);
    }

    private void navigateOnboardFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_onboardFragment);
    }

    private void navigatePlaylistFragment(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_libraryFragment_to_playlistFragment, bundle);
    }

    private void navigatePlaylistEditFragment(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("playlist", playlist);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_libraryFragment_to_playlistEditFragment, bundle);
    }

}
