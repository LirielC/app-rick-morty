package com.example.rickandmortyapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.ui.state.LoginUiState;
import com.example.rickandmortyapp.ui.menu.MainMenuActivity;
import com.example.rickandmortyapp.util.UiState;
import com.example.rickandmortyapp.viewmodel.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private LinearLayout loadingContainer;
    private TextView errorTextView;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        if (viewModel.hasActiveSession()) {
            navigateToMainMenu();
            return;
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        progressBar = findViewById(R.id.loginProgressBar);
        loadingContainer = findViewById(R.id.loginLoadingContainer);
        errorTextView = findViewById(R.id.loginErrorTextView);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> viewModel.login(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString()
        ));

        viewModel.getLoginUiState().observe(this, this::renderState);
    }

    private void renderState(LoginUiState screenState) {
        UiState<?> state = screenState.getLoginState();
        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
        emailEditText.setEnabled(!isLoading);
        passwordEditText.setEnabled(!isLoading);
        errorTextView.setVisibility(View.GONE);

        if (state.getStatus() == UiState.Status.ERROR || state.getStatus() == UiState.Status.EMPTY) {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(mapLoginMessage(state.getMessage()));
        }

        String errorMessage = screenState.getErrorMessageEvent() != null
                ? screenState.getErrorMessageEvent().getContentIfNotHandled()
                : null;
        if (errorMessage != null) {
            Snackbar.make(loginButton, mapLoginMessage(errorMessage), Snackbar.LENGTH_LONG).show();
        }

        Boolean shouldNavigate = screenState.getNavigationEvent() != null
                ? screenState.getNavigationEvent().getContentIfNotHandled()
                : null;
        if (Boolean.TRUE.equals(shouldNavigate)) {
            navigateToMainMenu();
        }
    }

    private void navigateToMainMenu() {
        startActivity(new Intent(this, MainMenuActivity.class));
        finishAffinity();
    }

    private String mapLoginMessage(String rawMessage) {
        if (rawMessage == null) {
            return getString(R.string.login_connection_error);
        }
        String message = rawMessage.toLowerCase();
        if (message.contains("e-mail e senha")) {
            return getString(R.string.login_empty_error);
        }
        if (message.contains("válido") || message.contains("valido")) {
            return getString(R.string.login_invalid_email_error);
        }
        if (message.contains("credenciais") || message.contains("inválid") || message.contains("invalid")) {
            return getString(R.string.login_invalid_credentials_error);
        }
        if (message.contains("backend") || message.contains("grails") || message.contains("autentic")) {
            return getString(R.string.login_connection_error);
        }
        return getString(R.string.login_connection_error);
    }
}
