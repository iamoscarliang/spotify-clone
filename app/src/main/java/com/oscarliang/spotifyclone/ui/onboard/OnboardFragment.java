package com.oscarliang.spotifyclone.ui.onboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentOnboardBinding;

public class OnboardFragment extends Fragment {

    private FragmentOnboardBinding mBinding;

    public OnboardFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentOnboardBinding.inflate(inflater, container, false);
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

        // Handle the back button event
        getActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // We make sure not navigate back to logout page
                getActivity().finish();
            }
        });

        mBinding.textSignup.setOnClickListener(v -> navigateSignupFragment());
        mBinding.textLogin.setOnClickListener(v -> navigateLoginFragment());
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
