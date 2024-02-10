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
import com.oscarliang.spotifyclone.util.AutoClearedValue;
import com.oscarliang.spotifyclone.util.Resource;

import java.util.Collections;

import javax.inject.Inject;

public class LibraryFragment extends Fragment implements Injectable,
        PlaylistInfoBottomSheet.PlaylistInfoBottomSheetCallback,
        CreatePlaylistDialog.onCreatePlaylistClickListener,
        DeletePlaylistDialog.OnConfirmDeletePlaylistClickListener {

    private AutoClearedValue<FragmentLibraryBinding> mBinding;
    private PlaylistAdapter mAdapter;
    private LibraryViewModel mViewModel;

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
        FragmentLibraryBinding viewBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(LibraryViewModel.class);

        initToolbar();
        initDrawer();
        initSwipeRefreshLayout();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setUser(mAuth.getCurrentUser().getUid());
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
        mViewModel.addPlaylist(mAuth.getCurrentUser().getUid(), playlistName);
    }

    @Override
    public void onConfirmDeletePlaylistClick(Playlist playlist) {
        mViewModel.deletePlaylist(mAuth.getCurrentUser().getUid(), playlist);
    }

    private void initToolbar() {
        mBinding.get().toolbar.setNavigationOnClickListener(view -> mBinding.get().drawerLayout.open());
        mBinding.get().toolbar.inflateMenu(R.menu.menu_library);
        mBinding.get().toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add_playlist) {
                showCreatePlaylistDialog();
                return true;
            }
            return false;
        });
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
        mAdapter = new PlaylistAdapter(
                playlist -> navigatePlaylistFragment(playlist),
                playlist -> showPlaylistInfoBottomSheet(playlist));
        mBinding.get().recyclerViewPlaylist.setAdapter(mAdapter);
        mBinding.get().recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private void subscribeObservers() {
        mViewModel.getPlaylists().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    mAdapter.submitList(resource.mData);
                    mBinding.get().shimmerLayoutPlaylist.stopShimmer();
                    mBinding.get().shimmerLayoutPlaylist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(mBinding.get().layoutContent, resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mAdapter.submitList(Collections.emptyList());
                    mBinding.get().shimmerLayoutPlaylist.startShimmer();
                    mBinding.get().shimmerLayoutPlaylist.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getAddPlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource != null) {
                switch (resource.mState) {
                    case SUCCESS:
                        mViewModel.refresh();
                        String msg = getString(R.string.playlist_create, resource.mData.getName());
                        Snackbar.make(mBinding.get().layoutContent, msg, Snackbar.LENGTH_LONG).show();
                        break;
                    case ERROR:
                        Snackbar.make(mBinding.get().layoutContent, resource.mMessage, Snackbar.LENGTH_LONG).show();
                        break;
                    case LOADING:
                        // Ignore
                        break;
                }
            }
        });
        mViewModel.getDeletePlaylistState().observe(getViewLifecycleOwner(), event -> {
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource != null) {
                switch (resource.mState) {
                    case SUCCESS:
                        mViewModel.refresh();
                        String msg = getString(R.string.playlist_delete, resource.mData.getName());
                        Snackbar.make(mBinding.get().layoutContent, msg, Snackbar.LENGTH_LONG).show();
                        break;
                    case ERROR:
                        Snackbar.make(mBinding.get().layoutContent, resource.mMessage, Snackbar.LENGTH_LONG).show();
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
