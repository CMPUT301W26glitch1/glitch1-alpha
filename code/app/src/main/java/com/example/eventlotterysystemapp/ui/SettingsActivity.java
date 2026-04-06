package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchOptOut, switchAccessibility;
    private FirebaseFirestore db;
    private String userDocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();
        switchOptOut = findViewById(R.id.switchOptOut);
        switchAccessibility = findViewById(R.id.switchAccessibility);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(RESULT_OK);
                finish();
            }
        });

        boolean accessibilityEnabled = AccessibilityUtils.isAccessibilityModeOn(this);
        switchAccessibility.setChecked(accessibilityEnabled);

        switchAccessibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AccessibilityUtils.saveAccessibilityMode(this, isChecked);
            String msg = isChecked ? "Accessibility mode enabled" : "Accessibility mode disabled";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            recreate();
        });

        // Look up the user's Firestore doc by email to get their doc ID and current preference
        String email = UserSession.getUser().getEmail();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        userDocId = querySnapshot.getDocuments().get(0).getId();
                        Boolean optedOut = querySnapshot.getDocuments().get(0).getBoolean("notificationsOptedOut");
                        switchOptOut.setChecked(optedOut != null && optedOut);

                        // Only set the listener AFTER loading the current value to avoid a false trigger
                        switchOptOut.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            saveOptOutPreference(isChecked);
                        });
                    } else {
                        Toast.makeText(this, "Could not find user profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading settings", Toast.LENGTH_SHORT).show());
    }

    private void saveOptOutPreference(boolean optedOut) {
        if (userDocId == null) return;

        db.collection("users").document(userDocId)
                .update("notificationsOptedOut", optedOut)
                .addOnSuccessListener(unused -> {
                    UserSession.getUser().setNotificationsOptedOut(optedOut);
                    String msg = optedOut ? "Notifications disabled" : "Notifications enabled";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save setting", Toast.LENGTH_SHORT).show());
    }
}