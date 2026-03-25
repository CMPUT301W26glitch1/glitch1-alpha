package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.organizer.OrganizerMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// LoginActivity is the entry point of the app.
// It handles authentication for all roles: Admin, Organizer, and Entrant.
public class LoginActivity extends AppCompatActivity {

    // UI fields for email and password input
    private EditText etEmail, etPassword;

    // Buttons for logging in and navigating to registration
    private Button btnContinue, btnSignUp;

    // Firestore database instance used to query the users collection
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firestore instance
        db = FirebaseFirestore.getInstance();

        // Bind UI elements to their corresponding views in activity_login.xml
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnContinue = findViewById(R.id.btnContinue);
        btnSignUp   = findViewById(R.id.btnSignUp);

        // When Continue is clicked, run the login logic
        btnContinue.setOnClickListener(v -> handleLogin());

        // When Sign Up is clicked, navigate to RegistrationActivity
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class)));
    }

    // handleLogin() reads the input fields, validates them,
    // then queries Firestore to authenticate the user.
    private void handleLogin() {
        // Read and trim whitespace from the input fields
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic validation: reject empty fields before hitting Firestore
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ANDROID_ID used to track which device an Entrant/Organizer last logged in from.
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Query Firestore: find a user document where both email AND password match.
        // whereEqualTo chains act as AND conditions on the query.
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", hashPassword(password))
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // If no documents matched, the credentials are wrong
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Take the first matching document (emails should be unique)
                    QueryDocumentSnapshot userDoc =
                            (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);

                    // Read the role and document ID from the matched user
                    String role   = userDoc.getString("role");
                    String userId = userDoc.getId();

                    // Admins do not get device tracking. Only Entrants and Organizers
                    // have their lastDeviceId updated so the app knows which device they use.
                    if (!"Admin".equals(role)) {
                        db.collection("users").document(userId)
                                .update("lastDeviceId", deviceId);
                    }

                    // Route the user to the correct dashboard based on their role
                    switch (role != null ? role : "") {
                        case "Admin":
                            // Navigate to AdminDashboardActivity
                            startActivity(new Intent(this, AdminDashboardActivity.class));
                            finish();
                            break;
                        case "Organizer":
                            Intent intent = new Intent(this, OrganizerMainActivity.class);
                            // Pass the email as a string extra
                            intent.putExtra("USER_EMAIL", email);
                            startActivity(intent);
                            finish();
                            break;
                        case "Entrant":
                            // TODO: replace with EntrantDashboardActivity when built
                            Toast.makeText(this, "Welcome, Entrant!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, EventListActivity.class));
                            finish();

                            break;
                        default:
                            // Role field is missing or unrecognized
                            Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                    }

                  //  finish();
                })
                // If the Firestore call itself fails (e.g. no internet), show the error
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
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