package com.oscarliang.spotifyclone.ui.signup;

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
import com.google.firebase.auth.AuthResult;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.FragmentSignupBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.util.AutoClearedValue;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class SignupFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mFactory;

    private AutoClearedValue<FragmentSignupBinding> mBinding;
    private SignupViewModel mViewModel;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSignupBinding viewBinding = FragmentSignupBinding.inflate(inflater, container, false);
        mBinding = new AutoClearedValue<>(this, viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this, mFactory).get(SignupViewModel.class);

        mBinding.get().btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        mBinding.get().btnNext.setOnClickListener(v -> signup());
        mBinding.get().btnNext.setEnabled(false);

        initEditText();
        subscribeObservers();
        showSoftKeyBoard();
    }

    private void initEditText() {
        mBinding.get().editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                String password = mBinding.get().editPassword.getText().toString();
                if (password != null && password.length() > 0) {
                    mBinding.get().btnNext.setEnabled(charSequence != null && charSequence.length() > 0);
                }
            }
        });
        mBinding.get().editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                String email = mBinding.get().editEmail.getText().toString();
                if (email != null && email.length() > 0) {
                    mBinding.get().btnNext.setEnabled(charSequence != null && charSequence.length() != 0);
                }
            }
        });
    }

    private void subscribeObservers() {
        mViewModel.getCreateUserState().observe(getViewLifecycleOwner(), event -> {
            Resource<AuthResult> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    navigateSignupNameFragment();
                    mBinding.get().progressbar.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    mBinding.get().btnNext.setVisibility(View.VISIBLE);
                    mBinding.get().progressbar.setVisibility(View.GONE);
                    break;
                case LOADING:
                    mBinding.get().btnNext.setVisibility(View.INVISIBLE);
                    mBinding.get().progressbar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void signup() {
        hideSoftKeyBoard();
        String email = mBinding.get().editEmail.getText().toString().trim();
        String password = mBinding.get().editPassword.getText().toString().trim();
        mViewModel.signup(email, password);
    }

    private void showSoftKeyBoard() {
        mBinding.get().editEmail.requestFocus();
        WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.get().editEmail)
                .show(WindowInsetsCompat.Type.ime());
    }

    private void hideSoftKeyBoard() {
        if (mBinding.get().editEmail.hasFocus()) {
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.get().editEmail)
                    .hide(WindowInsetsCompat.Type.ime());
        } else if (mBinding.get().editPassword.hasFocus()) {
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.get().editPassword)
                    .hide(WindowInsetsCompat.Type.ime());
        }
    }

    private void navigateSignupNameFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_signupFragment_to_signupNameFragment);
    }

}
