package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserController;
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.example.eventlotterysystemapp.ui.organizer.OrganizerMainActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrationActivity extends AppCompatActivity {
    Button registerButton;
    EditText username, password, email, phoneNumber;
    Spinner roles;
    ArrayAdapter<String> rolesAdapter;
    UserController usersdb;
    Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton = findViewById(R.id.registerButton);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        roles = findViewById(R.id.roles);
        phoneNumber = findViewById(R.id.username2);
        returnButton = findViewById(R.id.returnButton);

        String[] rolesList = new String[]{"Entrant", "Organizer", "Admin"};
        rolesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rolesList);
        roles.setAdapter(rolesAdapter);

        usersdb = new UserController(this);

        registerButton.setOnClickListener(v -> handleRegistration());
        returnButton.setOnClickListener(v -> finish());
    }

    private void handleRegistration() {
        String rawPassword = password.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userRole = roles.getSelectedItem().toString();
        String userPhoneNumber = phoneNumber.getText().toString().trim();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (rawPassword.isEmpty() || userEmail.isEmpty()) {
            UiUtils.showNotification(this, "Error", "Fields cannot be empty");
            return;
        }

        String hashedPassword = hashPassword(rawPassword);

        User user = new User(
                username.getText().toString().trim(),
                hashedPassword,
                userEmail,
                userRole,
                deviceId
        );

        if (!userPhoneNumber.isEmpty()) {
            try {
                user.setPhoneNumber(userPhoneNumber);
            } catch (NumberFormatException e) {
                UiUtils.showNotification(this, "Error", "Invalid phone number");
                return;
            }
        }

        usersdb.checkUser(user, new UserController.UserCallback() {
            @Override
            public void onSuccess() {
                UiUtils.showNotification(RegistrationActivity.this, "Success", "User registered successfully!");

                Intent intent;
                if ("Admin".equals(userRole)) {
                    intent = new Intent(RegistrationActivity.this, AdminDashboardActivity.class);
                } else if ("Organizer".equals(userRole)) {
                    intent = new Intent(RegistrationActivity.this, OrganizerMainActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                } else {
                    UserSession.setUser(user);
                    intent = new Intent(RegistrationActivity.this, EventListActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                }

                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                UiUtils.showNotification(RegistrationActivity.this, "Error", message);
            }
        });
    }

    /**
     * Identical hashing logic to LoginActivity to ensure compatibility.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}