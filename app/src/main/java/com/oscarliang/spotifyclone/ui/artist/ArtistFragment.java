package com.oscarliang.spotifyclone.ui.artist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentArtistBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.ui.common.adapter.AlbumAdapter;
import com.oscarliang.spotifyclone.util.AutoClearedValue;

import javax.inject.Inject;

public class ArtistFragment extends Fragment implements Injectable {

    private static final String ARTIST_ID_KEY = "artist";

    private String mArtistId;

    private AutoClearedValue<FragmentArtistBinding> mBinding;
    private AlbumAdapter mAdapter;
    private ArtistViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mFactory;

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARTIST_ID_KEY)) {
            mArtistId = getArguments().getString(ARTIST_ID_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentArtistBinding viewBinding = FragmentArtistBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this, mFactory).get(ArtistViewModel.class);

        initToolbar();
        initRecyclerView();
        subscribeObservers();

        if (savedInstanceState == null) {
            mViewModel.setArtistId(mArtistId);
        }
    }

    private void initToolbar() {
        mBinding.get().toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());
    }

    private void initRecyclerView() {
        mAdapter = new AlbumAdapter(album -> navigateAlbumFragment(album.getId()));
        mBinding.get().recyclerViewAlbum.setAdapter(mAdapter);
        mBinding.get().recyclerViewAlbum.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private void subscribeObservers() {
        mViewModel.getArtist().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.mState) {
                case SUCCESS:
                    Glide.with(getContext())
                            .load(resource.mData.getImageUrl())
                            .placeholder(R.drawable.ic_artist)
                            .error(R.drawable.ic_artist)
                            .into(mBinding.get().imageArtist);
                    mBinding.get().collapsingToolbar.setTitle(resource.mData.getName());
                    mBinding.get().textBrowse.setVisibility(View.VISIBLE);
                    mBinding.get().shimmerLayoutArtist.stopShimmer();
                    mBinding.get().shimmerLayoutArtist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    mBinding.get().layoutLoadingStateArtist.layoutLoadingState.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateArtist.textMessage.setText(resource.mMessage);
                    break;
                case LOADING:
                    mBinding.get().imageArtist.setImageResource(0);
                    mBinding.get().collapsingToolbar.setTitle("");
                    mBinding.get().textBrowse.setVisibility(View.INVISIBLE);
                    mBinding.get().shimmerLayoutArtist.startShimmer();
                    mBinding.get().shimmerLayoutArtist.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateArtist.layoutLoadingState.setVisibility(View.GONE);
                    break;
            }
        });
        mViewModel.getAlbums().observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.mState) {
                case SUCCESS:
                    mAdapter.submitList(listResource.mData);
                    mBinding.get().shimmerLayoutAlbum.stopShimmer();
                    mBinding.get().shimmerLayoutAlbum.setVisibility(View.GONE);
                    break;
                case ERROR:
                    mBinding.get().layoutLoadingStateArtist.layoutLoadingState.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateArtist.textMessage.setText(listResource.mMessage);
                    break;
                case LOADING:
                    mBinding.get().shimmerLayoutAlbum.startShimmer();
                    mBinding.get().shimmerLayoutAlbum.setVisibility(View.VISIBLE);
                    mBinding.get().layoutLoadingStateArtist.layoutLoadingState.setVisibility(View.GONE);
                    break;
            }
        });
        mBinding.get().layoutLoadingStateArtist.btnRetry.setOnClickListener(view -> mViewModel.retry());
    }

    private void navigateAlbumFragment(String albumId) {
        Bundle bundle = new Bundle();
        bundle.putString("album", albumId);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_albumFragment, bundle);
    }

}
