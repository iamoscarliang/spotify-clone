package com.oscarliang.spotifyclone.feature.welcome;

import static java.util.Collections.singletonList;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.welcome.databinding.FragmentWelcomeBinding;

import javax.inject.Inject;

public class WelcomeFragment extends Fragment {

    @Inject
    AnalyticsLogger analyticsLogger;

    private AutoClearedValue<FragmentWelcomeBinding> binding;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        FragmentWelcomeBinding viewBinding = FragmentWelcomeBinding
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
        initButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_welcome)))
        );
    }

    private void initButton() {
        binding.get().btnSignup.setOnClickListener(view -> navigateSignupFragment());
        binding.get().btnLogin.setOnClickListener(view -> navigateLoginFragment());
    }

    private void navigateSignupFragment() {
        NavHostFragment
                .findNavController(this)
                .navigate(
                        Uri.parse("android-app://signupFragment"),
                        new NavOptions.Builder()
                                .setEnterAnim(R.anim.slide_in_horizontal)
                                .setExitAnim(R.anim.fade_out)
                                .setPopEnterAnim(R.anim.fade_in)
                                .setExitAnim(R.anim.slide_out_horizontal)
                                .build()
                );
    }

    private void navigateLoginFragment() {
        NavHostFragment
                .findNavController(this)
                .navigate(
                        Uri.parse("android-app://loginFragment"),
                        new NavOptions.Builder()
                                .setEnterAnim(R.anim.slide_in_horizontal)
                                .setExitAnim(R.anim.fade_out)
                                .setPopEnterAnim(R.anim.fade_in)
                                .setExitAnim(R.anim.slide_out_horizontal)
                                .build()
                );
    }

}