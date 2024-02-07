package com.oscarliang.spotifyclone.ui.onboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentOnboardBinding;
import com.oscarliang.spotifyclone.util.AutoClearedValue;

public class OnboardFragment extends Fragment {

    private AutoClearedValue<FragmentOnboardBinding> mBinding;

    public OnboardFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentOnboardBinding viewBinding = FragmentOnboardBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.get().textSignup.setOnClickListener(v -> navigateSignupFragment());
        mBinding.get().textLogin.setOnClickListener(v -> navigateLoginFragment());
    }

    private void navigateSignupFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_onboardFragment_to_signupFragment);
    }

    private void navigateLoginFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_onboardFragment_to_loginFragment);
    }

}
