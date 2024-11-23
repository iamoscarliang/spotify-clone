package com.oscarliang.spotifyclone;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.oscarliang.spotifyclone.core.auth.AuthManager;
import com.oscarliang.spotifyclone.core.player.MusicPlayer;
import com.oscarliang.spotifyclone.databinding.ActivityMainBinding;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> fragmentAndroidInjector;

    @Inject
    AuthManager authManager;

    @Inject
    MusicPlayer musicPlayer;

    private ActivityMainBinding binding;
    private NavController navController;
    private View navView;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentAndroidInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(com.oscarliang.spotifyclone.core.ui.R.style.Theme_Spotify);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();
        initDrawer();
        initNavController();
        initNavGraph();
        initNavView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscribeAuthState();
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initToolbar() {
        binding.btnMenu.setOnClickListener(view -> binding.drawerLayout.open());
    }

    private void initDrawer() {
        binding.drawer.navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                signOut();
            }
            item.setChecked(true);
            binding.drawerLayout.close();
            return true;
        });
    }

    private void initNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
    }

    private void initNavGraph() {
        // Setup the navGraph for navController
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        if (authManager.hasUser()) {
            // Navigate to home if user log in
            navGraph.setStartDestination(com.oscarliang.spotifyclone.feature.home.R.id.nav_graph_home);
        } else {
            // Navigate to onboarding otherwise
            navGraph.setStartDestination(com.oscarliang.spotifyclone.feature.welcome.R.id.nav_graph_welcome);
        }
        navController.setGraph(navGraph);
    }

    private void initNavView() {
        // Setup the nav view for different screen size
        if (binding.bottomNav != null) {
            // BottomNavView for default layout (portrait phone)
            navView = binding.bottomNav;
            NavigationUI.setupWithNavController(binding.bottomNav, navController);
        }
        if (binding.navRail != null) {
            // NavRail for 600dp width layout (landscape and foldable phone, portrait tablet)
            navView = binding.navRail;
            NavigationUI.setupWithNavController(binding.navRail, navController);
        }
        if (binding.navView != null) {
            // NavView for 960dp width layout (landscape tablet, desktop)
            navView = binding.navView;
            NavigationUI.setupWithNavController(binding.navView, navController);
        }

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            String label = destination.getParent().getLabel().toString();

            // Ignore any dialog
            if (label.contains("dialog") || label.contains("bottom sheet")) {
                return;
            }

            // Hide or show the toolbar
            if (Objects.equals(label, getString(com.oscarliang.spotifyclone.core.ui.R.string.feature_home))
                    || Objects.equals(label, getString(com.oscarliang.spotifyclone.core.ui.R.string.feature_search))
                    || Objects.equals(label, getString(com.oscarliang.spotifyclone.core.ui.R.string.feature_library))
            ) {
                showTopBar(label);
            } else {
                hideTopBar();
            }

            // Hide or show the nav view
            if (Objects.equals(label, getString(com.oscarliang.spotifyclone.core.ui.R.string.feature_player))
                    || Objects.equals(label, getString(com.oscarliang.spotifyclone.core.ui.R.string.feature_welcome))
                    || Objects.equals(label, getString(com.oscarliang.spotifyclone.core.ui.R.string.feature_signup))
                    || Objects.equals(label, getString(com.oscarliang.spotifyclone.core.ui.R.string.feature_login))
            ) {
                hideBottomBar();
                // Disable the drawer when user not sign in
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                showBottomBar();
                // Enable the drawer when user has sign in
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
    }

    private void subscribeAuthState() {
        disposables.add(authManager.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    TextView textUser = binding.drawer.navView
                            .getHeaderView(0)
                            .findViewById(R.id.text_user);
                    textUser.setText(user.getDisplayName());
                })
        );
    }

    private void signOut() {
        // Clear all the music to prevent playing
        // the music in bg when user is not log in
        musicPlayer.clearMusic();

        // Sign out and navigate to welcome page
        authManager.signOut();
        navigateWelcomeFragment();
    }

    private void navigateWelcomeFragment() {
        // Pop up all the backstack to prevent navigating back
        navController.navigate(
                Uri.parse("android-app://welcomeFragment"),
                new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()
        );
    }

    private void showTopBar(String label) {
        binding.toolbar.setVisibility(View.VISIBLE);
        binding.toolbar.setTitle(label);
    }

    private void hideTopBar() {
        binding.toolbar.setVisibility(View.GONE);
    }

    private void showBottomBar() {
        navView.setVisibility(View.VISIBLE);
        binding.miniPlayerHostFragment.setVisibility(View.VISIBLE);
    }

    private void hideBottomBar() {
        navView.setVisibility(View.GONE);
        binding.miniPlayerHostFragment.setVisibility(View.GONE);
    }

}