package com.oscarliang.spotifyclone.ui.signupname;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentSignupNameBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class SignupNameFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mFactory;

    private FragmentSignupNameBinding mBinding;
    private SignupNameViewModel mViewModel;

    public SignupNameFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentSignupNameBinding.inflate(inflater, container, false);
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
        mViewModel = new ViewModelProvider(this, mFactory).get(SignupNameViewModel.class);

        mBinding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        mBinding.btnSignup.setOnClickListener(v -> signupName());
        mBinding.btnSignup.setEnabled(false);

        initEditText();
        subscribeObservers();
        showSoftKeyBoard();
    }

    private void initEditText() {
        mBinding.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                mBinding.btnSignup.setEnabled(charSequence != null && charSequence.length() > 0);
            }
        });
    }

    private void subscribeObservers() {
        mViewModel.getUpdateProfileState().observe(getViewLifecycleOwner(), event -> {
            Resource<Void> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    navigateHomeFragment();
                    mBinding.progressbar.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    mBinding.btnSignup.setVisibility(View.VISIBLE);
                    mBinding.progressbar.setVisibility(View.GONE);
                    break;
                case LOADING:
                    mBinding.btnSignup.setVisibility(View.INVISIBLE);
                    mBinding.progressbar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void signupName() {
        hideSoftKeyBoard();
        String name = mBinding.editName.getText().toString().trim();
        mViewModel.setName(name);
    }

    private void showSoftKeyBoard() {
        mBinding.editName.requestFocus();
        WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.editName)
                .show(WindowInsetsCompat.Type.ime());
    }

    private void hideSoftKeyBoard() {
        if (mBinding.editName.hasFocus()) {
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.editName)
                    .hide(WindowInsetsCompat.Type.ime());
        }
    }

    private void navigateHomeFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_signupNameFragment_to_homeFragment);
    }

}
