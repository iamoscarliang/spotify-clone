package com.oscarliang.spotifyclone.ui.playlistedit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentPlaylistEditBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.util.MusicSorter;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class PlaylistEditFragment extends Fragment implements Injectable {

    private static final String PLAYLIST = "playlist";

    private Playlist mPlaylist;

    private FragmentPlaylistEditBinding mBinding;
    private MusicEditAdapter mAdapter;
    private PlaylistEditViewModel mViewModel;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    public PlaylistEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylist = getArguments().getParcelable(PLAYLIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPlaylistEditBinding.inflate(inflater, container, false);
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
        mViewModel = new ViewModelProvider(this, mFactory).get(PlaylistEditViewModel.class);

        initToolbar();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setPlaylistMusics(mPlaylist.getMusicIds());
        }
    }

    private void initToolbar() {
        mBinding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        mBinding.toolbar.inflateMenu(R.menu.menu_playlist_edit);
        mBinding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save_playlist) {
                savePlaylist();
                return true;
            }
            return false;
        });
    }

    private void initRecyclerView() {
        mAdapter = new MusicEditAdapter();
        mBinding.recyclerViewMusic.setAdapter(mAdapter);
        mBinding.recyclerViewMusic.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        int fromPosition = viewHolder.getAdapterPosition();
                        int toPosition = target.getAdapterPosition();
                        mAdapter.swapBetweenList(fromPosition, toPosition);
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Music music = mAdapter.removeFromList(position);
                        Snackbar.make(getView(), "Delete " + music.getTitle(), Snackbar.LENGTH_LONG)
                                .setAction("UNDO", view -> mAdapter.addToList(music, position))
                                .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_green, null))
                                .show();
                    }
                });
        helper.attachToRecyclerView(mBinding.recyclerViewMusic);
    }

    private void subscribeObservers() {
        mViewModel.getPlaylistMusics().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) {
                // Handler the case when returning absent livedata from an empty playlist
                mBinding.editPlaylistName.setText(mPlaylist.getName());
                mBinding.shimmerLayoutMusic.stopShimmer();
                mBinding.shimmerLayoutMusic.setVisibility(View.GONE);
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    mAdapter.submitList(MusicSorter.sort(resource.mData, mPlaylist.getMusicIds()));
                    mBinding.editPlaylistName.setText(mPlaylist.getName());
                    mBinding.shimmerLayoutMusic.stopShimmer();
                    mBinding.shimmerLayoutMusic.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    mBinding.editPlaylistName.setText("");
                    mBinding.shimmerLayoutMusic.startShimmer();
                    mBinding.shimmerLayoutMusic.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getUpdatePlaylistState().observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                // Handler the case when returning absent livedata from submitting unchanged playlist
                navigateUp();
                return;
            }
            Resource<Playlist> resource = event.getContentIfNotHandled();
            if (resource != null) {
                switch (resource.mState) {
                    case SUCCESS:
                        navigateUp();
                        break;
                    case ERROR:
                        Snackbar.make(getView(), "Error saving playlist " + resource.mData.getName(),
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case LOADING:
                        // Ignore
                        break;
                }
            }
        });
    }

    private void savePlaylist() {
        String playlistName = mBinding.editPlaylistName.getText().toString();
        if (playlistName != null && !playlistName.isEmpty()) {
            mViewModel.updatePlaylist(mAuth.getCurrentUser().getUid(), mPlaylist, playlistName, mAdapter.getList());
        } else {
            Snackbar.make(getView(), "Please enter a valid title", Snackbar.LENGTH_LONG).show();
        }
    }

    private void navigateUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }

}
