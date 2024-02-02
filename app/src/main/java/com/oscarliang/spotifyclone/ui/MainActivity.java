package com.oscarliang.spotifyclone.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.ActivityMainBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.ui.common.adapter.MusicBarAdapter;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class MainActivity extends AppCompatActivity implements Injectable, HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> mAndroidInjector;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ViewModelProvider.Factory mFactory;

    private ActivityMainBinding mBinding;
    private NavController mNavController;
    private View mNavView;
    private MusicBarAdapter mMusicBarAdapter;

    private MainViewModel mMainViewModel;
    private int mCurrentMusicPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMainViewModel = new ViewModelProvider(this, mFactory).get(MainViewModel.class);

        initNavController();
        initMusicBar();
        subscribeObservers();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return mAndroidInjector;
    }

    private void initNavController() {
        mNavController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment))
                .getNavController();
        // Setup the navGraph
        NavGraph navGraph = mNavController.getNavInflater().inflate(R.navigation.nav_graph);
        if (mAuth.getCurrentUser() != null) {
            // Navigate to home page if user log in
            navGraph.setStartDestination(R.id.homeFragment);
        } else {
            // Navigate to onboard page otherwise
            navGraph.setStartDestination(R.id.onboardFragment);
        }
        mNavController.setGraph(navGraph);

        // Setup the bottom nav view when portrait mode
        if (mBinding.bottomNav != null) {
            mNavView = mBinding.bottomNav;
            NavigationUI.setupWithNavController(mBinding.bottomNav, mNavController);
        }
        // Setup the nav view when landscape mode
        if (mBinding.navView != null) {
            mNavView = mBinding.navView;
            NavigationUI.setupWithNavController(mBinding.navView, mNavController);
        }

        // Hide or show the nav view and music bar when navigate to music fragment
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.musicFragment
                    || destination.getId() == R.id.onboardFragment
                    || destination.getId() == R.id.signupFragment
                    || destination.getId() == R.id.signupNameFragment
                    || destination.getId() == R.id.loginFragment) {
                mNavView.setVisibility(View.GONE);
                hideMusicBar();
            } else {
                mNavView.setVisibility(View.VISIBLE);
                showMusicBar();
            }
        });
    }

    private void initMusicBar() {
        mMusicBarAdapter = new MusicBarAdapter(music -> mNavController.navigate(R.id.action_to_musicFragment));
        mBinding.pager.setAdapter(mMusicBarAdapter);
        mBinding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position > mCurrentMusicPosition) {
                    mMainViewModel.skipNextMusic();
                } else {
                    mMainViewModel.skipPreviousMusic();
                }
                mCurrentMusicPosition = position;
            }
        });
        mBinding.btnPlay.setOnClickListener(view -> mMainViewModel.toggleMusic());
    }

    private void subscribeObservers() {
        mMainViewModel.getMusics().observe(this, musics -> mMusicBarAdapter.submitList(musics));
        mMainViewModel.getCurrentMusic().observe(this, currentMusic ->
                switchPagerToCurrentMusic(mMainViewModel.getCurrentIndex()));
        mMainViewModel.getPlaying().observe(this, isPlaying ->
                mBinding.btnPlay.setBackgroundResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play));
    }

    private void switchPagerToCurrentMusic(int position) {
        if (position != -1) {
            mBinding.pager.setCurrentItem(position);
            mCurrentMusicPosition = position;
        }
    }

    private void showMusicBar() {
        mBinding.imagePagerBg.setVisibility(View.VISIBLE);
        mBinding.pager.setVisibility(View.VISIBLE);
        mBinding.btnPlay.setVisibility(View.VISIBLE);
    }

    private void hideMusicBar() {
        mBinding.imagePagerBg.setVisibility(View.GONE);
        mBinding.pager.setVisibility(View.GONE);
        mBinding.btnPlay.setVisibility(View.GONE);
    }

}
