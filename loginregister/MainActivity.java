package com.example.loginregister;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private MaterialButton loginButton;
    private MaterialButton signupButton;
    private TextView forgotPasswordText;
    private ImageView logoImage;
    private CardView mainCard;
    private View connectionPointRed;
    private View connectionPointBlue;
    private View strengthBar1, strengthBar2, strengthBar3;
    private TextView strengthText;
    private View passwordStrengthLayout;

    // Database helper
    private DbHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        DB = new DbHelper(this);

        initializeViews();
        setupAnimations();
        setupPasswordValidation();
        setupClickListeners();
    }

    private void initializeViews() {
        // Using your existing IDs
        usernameLayout = findViewById(R.id.username1);
        passwordLayout = findViewById(R.id.Password1);
        usernameEditText = findViewById(R.id.username_EditText);
        passwordEditText = findViewById(R.id.Password_EditText);
        loginButton = findViewById(R.id.btnsignin1);
        signupButton = findViewById(R.id.btnsignup1);
        forgotPasswordText = findViewById(R.id.btnforgot);

        logoImage = findViewById(R.id.logo_image);
        mainCard = findViewById(R.id.main_card);
        connectionPointRed = findViewById(R.id.connection_point_red);
        connectionPointBlue = findViewById(R.id.connection_point_blue);
        strengthBar1 = findViewById(R.id.strength_bar_1);
        strengthBar2 = findViewById(R.id.strength_bar_2);
        strengthBar3 = findViewById(R.id.strength_bar_3);
        strengthText = findViewById(R.id.strength_text);
        passwordStrengthLayout = findViewById(R.id.password_strength_layout);
    }

    private void setupAnimations() {
        // Only setup animations if views exist
        if (logoImage != null) {
            Animation logoPulse = AnimationUtils.loadAnimation(this, R.anim.logo_pulse);
            logoImage.startAnimation(logoPulse);
        }

        if (connectionPointRed != null && connectionPointBlue != null) {
            Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
            connectionPointRed.startAnimation(blinkAnimation);

            new Handler().postDelayed(() -> {
                connectionPointBlue.startAnimation(blinkAnimation);
            }, 500);
        }

        if (mainCard != null) {
            Animation slideInUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
            new Handler().postDelayed(() -> {
                mainCard.setVisibility(View.VISIBLE);
                mainCard.startAnimation(slideInUp);
                mainCard.setAlpha(1.0f);
            }, 300);
        }
    }

    private void setupPasswordValidation() {
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        TextWatcher usernameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateUsername(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        passwordEditText.addTextChangedListener(passwordWatcher);
        usernameEditText.addTextChangedListener(usernameWatcher);
    }

    private void validatePassword(String password) {
        if (passwordStrengthLayout == null) return; // Skip if UI elements don't exist

        if (password.isEmpty()) {
            passwordStrengthLayout.setVisibility(View.GONE);
            passwordLayout.setError(null);
            passwordLayout.setHelperText(null);
            return;
        }

        passwordStrengthLayout.setVisibility(View.VISIBLE);

        boolean hasMinLength = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");

        int strength = 0;
        if (hasMinLength) strength++;
        if (hasUppercase) strength++;
        if (hasNumber) strength++;

        updateStrengthBars(strength);

        if (strength == 3) {
            passwordLayout.setError(null);
            passwordLayout.setHelperText("Strong password ✓");
            passwordLayout.setHelperTextColor(getColorStateList(android.R.color.holo_green_dark));
            if (strengthText != null) {
                strengthText.setText("Strong");
                strengthText.setTextColor(getColor(android.R.color.holo_green_dark));
            }
        } else {
            StringBuilder errorMessage = new StringBuilder("Password needs: ");
            if (!hasMinLength) errorMessage.append("8+ characters, ");
            if (!hasUppercase) errorMessage.append("1 uppercase, ");
            if (!hasNumber) errorMessage.append("1 number, ");

            String finalError = errorMessage.toString();
            finalError = finalError.substring(0, finalError.length() - 2);

            passwordLayout.setError(finalError);
            passwordLayout.setHelperText(null);

            if (strengthText != null) {
                if (strength == 1) {
                    strengthText.setText("Weak");
                    strengthText.setTextColor(getColor(android.R.color.holo_red_dark));
                } else {
                    strengthText.setText("Medium");
                    strengthText.setTextColor(getColor(android.R.color.holo_orange_dark));
                }
            }
        }
    }

    private void updateStrengthBars(int strength) {
        if (strengthBar1 == null || strengthBar2 == null || strengthBar3 == null) return;

        // Reset all bars
        strengthBar1.setBackgroundColor(getColor(android.R.color.darker_gray));
        strengthBar2.setBackgroundColor(getColor(android.R.color.darker_gray));
        strengthBar3.setBackgroundColor(getColor(android.R.color.darker_gray));

        // Animate bars based on strength
        ObjectAnimator animator1 = null, animator2 = null, animator3 = null;

        if (strength >= 1) {
            strengthBar1.setBackgroundColor(getColor(android.R.color.holo_red_dark));
            animator1 = ObjectAnimator.ofFloat(strengthBar1, "scaleX", 0f, 1f);
        }
        if (strength >= 2) {
            strengthBar2.setBackgroundColor(getColor(android.R.color.holo_orange_dark));
            animator2 = ObjectAnimator.ofFloat(strengthBar2, "scaleX", 0f, 1f);
        }
        if (strength >= 3) {
            strengthBar3.setBackgroundColor(getColor(android.R.color.holo_green_dark));
            animator3 = ObjectAnimator.ofFloat(strengthBar3, "scaleX", 0f, 1f);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        if (animator1 != null && animator2 != null && animator3 != null) {
            animatorSet.playSequentially(animator1, animator2, animator3);
        } else if (animator1 != null && animator2 != null) {
            animatorSet.playSequentially(animator1, animator2);
        } else if (animator1 != null) {
            animatorSet.play(animator1);
        }

        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void validateUsername(String username) {
        if (username.isEmpty()) {
            usernameLayout.setError(null);
            return;
        }

        if (username.length() < 3) {
            usernameLayout.setError("Username must be at least 3 characters");
        } else {
            usernameLayout.setError(null);
        }
    }

    private void updateLoginButtonState() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isUsernameValid = username.length() >= 3;
        boolean isPasswordValid = password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*");

        if (isUsernameValid && isPasswordValid) {
            loginButton.setEnabled(true);
            loginButton.setAlpha(1.0f);

            // Add subtle animation to indicate button is ready
            ObjectAnimator.ofFloat(loginButton, "scaleX", 1.0f, 1.05f, 1.0f).setDuration(300).start();
            ObjectAnimator.ofFloat(loginButton, "scaleY", 1.0f, 1.05f, 1.0f).setDuration(300).start();
        } else {
            loginButton.setEnabled(true); // Keep enabled for basic validation
            loginButton.setAlpha(0.8f);
        }
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Button press animation
                try {
                    Animation buttonScale = AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale);
                    v.startAnimation(buttonScale);
                } catch (Exception e) {
                    // Animation file not found, continue without animation
                }

                String user = usernameEditText.getText().toString().trim();
                String pass = passwordEditText.getText().toString().trim();

                // Basic validation
                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show loading state
                loginButton.setText("CONNECTING...");
                loginButton.setEnabled(false);

                // Simulate network delay for better UX
                new Handler().postDelayed(() -> {
                    // Database validation using your existing DbHelper
                    Boolean checkuserpass = DB.checkusernamepassword(user, pass);

                    if (checkuserpass == true) {
                        Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();

                        // Redirect to HomeActivity (your existing activity)
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish(); // Close login activity
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                        // Reset button state
                        loginButton.setText("LOG IN");
                        loginButton.setEnabled(true);
                    }
                }, 1000); // 1 second delay for better UX
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Animation buttonScale = AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale);
                    view.startAnimation(buttonScale);
                } catch (Exception e) {
                    // Animation file not found, continue without animation
                }

                // Redirect to LoginActivity (your existing registration activity)
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to PasswordActivity (your existing forgot password activity)
                Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
            }
        });

        // Add focus animations for input fields
        usernameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ObjectAnimator.ofFloat(usernameLayout, "scaleX", 1.0f, 1.02f).setDuration(200).start();
                ObjectAnimator.ofFloat(usernameLayout, "scaleY", 1.0f, 1.02f).setDuration(200).start();
            } else {
                ObjectAnimator.ofFloat(usernameLayout, "scaleX", 1.02f, 1.0f).setDuration(200).start();
                ObjectAnimator.ofFloat(usernameLayout, "scaleY", 1.02f, 1.0f).setDuration(200).start();
            }
        });

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ObjectAnimator.ofFloat(passwordLayout, "scaleX", 1.0f, 1.02f).setDuration(200).start();
                ObjectAnimator.ofFloat(passwordLayout, "scaleY", 1.0f, 1.02f).setDuration(200).start();
            } else {
                ObjectAnimator.ofFloat(passwordLayout, "scaleX", 1.02f, 1.0f).setDuration(200).start();
                ObjectAnimator.ofFloat(passwordLayout, "scaleY", 1.02f, 1.0f).setDuration(200).start();
            }
        });
    }
}