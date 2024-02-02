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
import com.oscarliang.spotifyclone.util.Resource;

import javax.inject.Inject;

public class SignupFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mFactory;

    private FragmentSignupBinding mBinding;
    private SignupViewModel mViewModel;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentSignupBinding.inflate(inflater, container, false);
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
        mViewModel = new ViewModelProvider(this, mFactory).get(SignupViewModel.class);

        mBinding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        mBinding.btnNext.setOnClickListener(v -> signup());
        mBinding.btnNext.setEnabled(false);

        initEditText();
        subscribeObservers();
        showSoftKeyBoard();
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
                    mBinding.btnNext.setEnabled(charSequence != null && charSequence.length() > 0);
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
                    mBinding.btnNext.setEnabled(charSequence != null && charSequence.length() != 0);
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
                    mBinding.progressbar.setVisibility(View.GONE);
                    break;
                case ERROR:
                    Snackbar.make(getView(), resource.mMessage, Snackbar.LENGTH_LONG).show();
                    mBinding.btnNext.setVisibility(View.VISIBLE);
                    mBinding.progressbar.setVisibility(View.GONE);
                    break;
                case LOADING:
                    mBinding.btnNext.setVisibility(View.INVISIBLE);
                    mBinding.progressbar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void signup() {
        hideSoftKeyBoard();
        String email = mBinding.editEmail.getText().toString().trim();
        String password = mBinding.editPassword.getText().toString().trim();
        mViewModel.createUser(email, password);
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

    private void navigateSignupNameFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_signupFragment_to_signupNameFragment);
    }

}
