package com.oscarliang.spotifyclone.ui.login;

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
import com.oscarliang.spotifyclone.databinding.FragmentLoginBinding;
import com.oscarliang.spotifyclone.di.Injectable;
import com.oscarliang.spotifyclone.ui.common.dialog.ResetPasswordDialog;
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class LoginFragment extends Fragment implements Injectable,
        ResetPasswordDialog.onSendResetPasswordEmailClickListener {

    @Inject
    ViewModelProvider.Factory mFactory;

    private FragmentLoginBinding mBinding;
    private LoginViewModel mViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
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
        mViewModel = new ViewModelProvider(this, mFactory).get(LoginViewModel.class);

        mBinding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        mBinding.textForgotPassword.setOnClickListener(v -> showResetPasswordDialog());
        mBinding.btnLogin.setOnClickListener(v -> login());
        mBinding.btnLogin.setEnabled(false);

        initEditText();
        subscribeObservers();
        showSoftKeyBoard();
    }

    @Override
    public void onSendResetPasswordEmailClick(String email) {
        mViewModel.resetPassword(email);
    }

    private void initEditText() {
        mBinding.editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                String password = mBinding.editPassword.getText().toString();
                if (password != null && password.length() > 0) {
                    mBinding.btnLogin.setEnabled(charSequence != null && charSequence.length() > 0);
                }
            }
        });
        mBinding.editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                String email = mBinding.editEmail.getText().toString();
                if (email != null && email.length() > 0) {
                    mBinding.btnLogin.setEnabled(charSequence != null && charSequence.length() != 0);
                }
            }
        });
    }

    private void subscribeObservers() {
        mViewModel.getLoginUserState().observe(getViewLifecycleOwner(), event -> {
            Resource<AuthResult> resource = event.getContentIfNotHandled();
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
                    mBinding.btnLogin.setVisibility(View.VISIBLE);
                    mBinding.progressbar.setVisibility(View.GONE);
                    break;
                case LOADING:
                    mBinding.btnLogin.setVisibility(View.INVISIBLE);
                    mBinding.progressbar.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mViewModel.getResetPasswordState().observe(getViewLifecycleOwner(), event -> {
            Resource<Void> resource = event.getContentIfNotHandled();
            if (resource == null) {
                return;
            }
            switch (resource.mState) {
                case SUCCESS:
                    Snackbar.make(getView(), "Send reset email", Snackbar.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Ignore
                    break;
            }
        });
    }

    private void login() {
        hideSoftKeyBoard();
        String email = mBinding.editEmail.getText().toString();
        String password = mBinding.editPassword.getText().toString();
        mViewModel.loginUser(email, password);
    }

    private void showSoftKeyBoard() {
        mBinding.editEmail.requestFocus();
        WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.editEmail)
                .show(WindowInsetsCompat.Type.ime());
    }

    private void hideSoftKeyBoard() {
        if (mBinding.editEmail.hasFocus()) {
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.editEmail)
                    .hide(WindowInsetsCompat.Type.ime());
        } else if (mBinding.editPassword.hasFocus()) {
            WindowCompat.getInsetsController(getActivity().getWindow(), mBinding.editPassword)
                    .hide(WindowInsetsCompat.Type.ime());
        }
    }

    private void showResetPasswordDialog() {
        ResetPasswordDialog dialog = new ResetPasswordDialog();
        dialog.show(getChildFragmentManager(), null);
    }

    private void navigateHomeFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_loginFragment_to_homeFragment);
    }

}
