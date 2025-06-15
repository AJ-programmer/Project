package com.example.loginregister;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetActivity extends AppCompatActivity {

    private TextInputEditText usernameDisplay, passwordEditText, repasswordEditText;
    private TextInputLayout usernameLayout, passwordLayout, repasswordLayout;
    private MaterialButton confirmButton, backLoginButton;
    private View strengthBar1, strengthBar2, strengthBar3;
    private android.widget.TextView strengthText;
    private View passwordStrengthLayout;
    private CardView mainCard;
    private DbHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        initializeViews();
        setupDatabase();
        setupUsername();
        setupPasswordValidation();
        setupClickListeners();
        animateCardIn();
    }

    private void initializeViews() {
        mainCard = findViewById(R.id.main_card);
        usernameDisplay = findViewById(R.id.username_display_EditText);
        usernameLayout = findViewById(R.id.username_display);
        passwordEditText = findViewById(R.id.password_reset);
        passwordLayout = findViewById(R.id.Password);
        repasswordEditText = findViewById(R.id.repassword_reset);
        repasswordLayout = findViewById(R.id.Repassword);
        confirmButton = findViewById(R.id.confirm_reset);
        backLoginButton = findViewById(R.id.backToLogin);

        // Password strength indicators
        passwordStrengthLayout = findViewById(R.id.password_strength_layout);
        strengthBar1 = findViewById(R.id.strength_bar_1);
        strengthBar2 = findViewById(R.id.strength_bar_2);
        strengthBar3 = findViewById(R.id.strength_bar_3);
        strengthText = findViewById(R.id.strength_text);
    }

    private void setupDatabase() {
        DB = new DbHelper(this);
    }

    private void setupUsername() {
        Intent intent = getIntent();
        String usernameValue = intent.getStringExtra("username");
        if (usernameValue != null) {
            usernameDisplay.setText(usernameValue);
        } else {
            Toast.makeText(ResetActivity.this, "Error: Username not received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void setupPasswordValidation() {
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        repasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordMatch();
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validatePassword(String password) {
        if (password.isEmpty()) {
            passwordStrengthLayout.setVisibility(View.GONE);
            passwordLayout.setError(null);
            return;
        }

        passwordStrengthLayout.setVisibility(View.VISIBLE);

        int strength = calculatePasswordStrength(password);
        updatePasswordStrengthUI(strength);

        if (!isPasswordValid(password)) {
            passwordLayout.setError("Password must be 8+ characters with 1 uppercase and 1 number");
        } else {
            passwordLayout.setError(null);
        }
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;


        if (password.length() >= 8) strength++;


        if (password.matches(".*[A-Z].*")) strength++;


        if (password.matches(".*[0-9].*")) strength++;

        return strength;
    }

    private void updatePasswordStrengthUI(int strength) {

        strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        switch (strength) {
            case 1:
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                strengthText.setText("Weak");
                strengthText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                break;
            case 2:
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                strengthText.setText("Medium");
                strengthText.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                break;
            case 3:
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                strengthText.setText("Strong");
                strengthText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                break;
            default:
                strengthText.setText("Weak");
                strengthText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                break;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*");
    }

    private void validatePasswordMatch() {
        String password = passwordEditText.getText().toString();
        String repassword = repasswordEditText.getText().toString();

        if (!repassword.isEmpty()) {
            if (!password.equals(repassword)) {
                repasswordLayout.setError("Passwords do not match");
            } else {
                repasswordLayout.setError(null);
            }
        } else {
            repasswordLayout.setError(null);
        }
    }

    private void validateForm() {
        String password = passwordEditText.getText().toString();
        String repassword = repasswordEditText.getText().toString();

        boolean isPasswordValid = isPasswordValid(password);
        boolean isPasswordMatching = password.equals(repassword) && !repassword.isEmpty();
        boolean isFormValid = isPasswordValid && isPasswordMatching;

        confirmButton.setEnabled(isFormValid);
        confirmButton.setAlpha(isFormValid ? 1.0f : 0.5f);
    }

    private void setupClickListeners() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        backLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void resetPassword() {
        String username = usernameDisplay.getText().toString();
        String password = passwordEditText.getText().toString();
        String repassword = repasswordEditText.getText().toString();


        if (password.isEmpty() || repassword.isEmpty()) {
            showToast("Please enter all fields");
            return;
        }

        if (!isPasswordValid(password)) {
            showToast("Password must be 8+ characters with 1 uppercase and 1 number");
            return;
        }

        if (!password.equals(repassword)) {
            showToast("Passwords do not match");
            return;
        }

        confirmButton.setEnabled(false);
        confirmButton.setText("UPDATING...");

        try {
            Boolean success = DB.updatePassword(username, password);

            if (success) {
                showToast("Password updated successfully");
                navigateToLoginWithSuccess();
            } else {
                showToast("Failed to update password. Please try again.");
                resetButtonState();
            }
        } catch (Exception e) {
            showToast("An error occurred. Please try again.");
            resetButtonState();
        }
    }

    private void resetButtonState() {
        confirmButton.setEnabled(true);
        confirmButton.setText("RESET PASSWORD");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginWithSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("password_reset_success", true);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(ResetActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void animateCardIn() {

        ObjectAnimator translateY = ObjectAnimator.ofFloat(mainCard, "translationY", 50f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mainCard, "alpha", 0f, 1f);

        translateY.setDuration(600);
        alpha.setDuration(600);

        translateY.start();
        alpha.start();
    }
}