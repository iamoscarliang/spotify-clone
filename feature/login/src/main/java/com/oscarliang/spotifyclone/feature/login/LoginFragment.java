package com.oscarliang.spotifyclone.feature.login;

import static java.util.Collections.singletonList;

import android.annotation.SuppressLint;
import android.net.Uri;
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
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsEvent;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsLogger;
import com.oscarliang.spotifyclone.core.analytics.AnalyticsParam;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.dialog.ResetPasswordDialog;
import com.oscarliang.spotifyclone.core.ui.util.AutoClearedValue;
import com.oscarliang.spotifyclone.feature.login.databinding.FragmentLoginBinding;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginFragment extends Fragment implements
        ResetPasswordDialog.onSendResetPasswordEmailClickListener {

    @Inject
    AnalyticsLogger analyticsLogger;

    @Inject
    ViewModelProvider.Factory factory;

    private AutoClearedValue<FragmentLoginBinding> binding;
    private LoginViewModel viewModel;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public LoginFragment() {
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
        FragmentLoginBinding viewBinding = FragmentLoginBinding
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
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        initToolbar();
        initButton();
        initEditText();
        showSoftKeyBoard();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeObserver();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log screen view event
        analyticsLogger.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                singletonList(new AnalyticsParam(AnalyticsParam.SCREEN_NAME, getString(R.string.feature_login)))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    @Override
    public void onSendResetPasswordEmailClick(String email) {
        disposables.add(viewModel.resetPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            String msg = getString(R.string.reset_password_email_sand);
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
                        },
                        throwable -> {
                            String msg = throwable.getMessage();
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
                        }
                )
        );
    }

    private void initToolbar() {
        binding.get().toolbar.setNavigationOnClickListener(view ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void initButton() {
        binding.get().btnLogin.setOnClickListener(view -> login());
        binding.get().btnForgotPassword.setOnClickListener(view -> showResetPasswordDialog());
    }

    private void initEditText() {
        binding.get().editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                viewModel.setEmail(charSequence.toString().trim());
            }
        });
        binding.get().editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                viewModel.setPassword(charSequence.toString().trim());
            }
        });
    }

    private void subscribeObserver() {
        disposables.add(viewModel.getLoginEnable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isEnable -> {
                    binding.get().btnLogin.setEnabled(isEnable);
                    binding.get().btnLogin.setBackgroundColor(
                            getResources().getColor(isEnable ? R.color.white : R.color.gray_500)
                    );
                })
        );
    }

    private void login() {
        hideSoftKeyBoard();
        binding.get().btnLogin.setVisibility(View.INVISIBLE);
        binding.get().progressbar.setVisibility(View.VISIBLE);

        String email = binding.get().editEmail.getText().toString().trim();
        String password = binding.get().editPassword.getText().toString().trim();
        disposables.add(viewModel.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> navigateHomeFragment(),
                        throwable -> {
                            String msg = throwable.getMessage();
                            Snackbar.make(binding.get().getRoot(), msg, Snackbar.LENGTH_LONG).show();
                            binding.get().btnLogin.setVisibility(View.VISIBLE);
                            binding.get().progressbar.setVisibility(View.GONE);
                        }
                )
        );
    }

    private void showSoftKeyBoard() {
        binding.get().editEmail.requestFocus();
        WindowCompat
                .getInsetsController(getActivity().getWindow(), binding.get().editEmail)
                .show(WindowInsetsCompat.Type.ime());
    }

    private void hideSoftKeyBoard() {
        View focusView;
        if (binding.get().editEmail.hasFocus()) {
            focusView = binding.get().editEmail;
        } else {
            focusView = binding.get().editPassword;
        }
        focusView.clearFocus();
        WindowCompat
                .getInsetsController(getActivity().getWindow(), focusView)
                .hide(WindowInsetsCompat.Type.ime());
    }

    private void showResetPasswordDialog() {
        ResetPasswordDialog dialog = new ResetPasswordDialog();
        dialog.show(getChildFragmentManager(), null);
    }

    @SuppressLint("DiscouragedApi")
    private void navigateHomeFragment() {
        // Pop up all the backstack to prevent navigating back
        int destinationId = getResources().getIdentifier(
                "nav_graph",
                "id",
                getContext().getPackageName()
        );
        NavHostFragment
                .findNavController(this)
                .navigate(
                        Uri.parse("android-app://homeFragment"),
                        new NavOptions.Builder()
                                .setPopUpTo(destinationId, true)
                                .build()
                );
    }

}