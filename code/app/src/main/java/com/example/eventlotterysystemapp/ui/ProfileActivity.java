package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class ProfileActivity extends AppCompatActivity {
    EditText etName, etEmail, etPhone;
    Button btnSave, btnDelete;
    User user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AccessibilityUtils.applyAccessibilityMode(this);

        db = FirebaseFirestore.getInstance();

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDeleteProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        user = UserSession.getUser();
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhoneNumber());
        }

        btnDelete.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure? This will permanently remove your profile data.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteUserProfile())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnSave.setOnClickListener(v -> {
            if (etEmail.getText().toString().isEmpty()) {
                etEmail.setError("Email required");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                etEmail.setError("Invalid email");
                return;
            }

            if (etName.getText().toString().isEmpty()) {
                etName.setError("Name required");
                return;
            }

            String originalEmail = UserSession.getUser().getEmail();
            String newEmail = etEmail.getText().toString();

            db.collection("users")
                    .whereEqualTo("email", originalEmail)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            doc.getReference().delete();
                        }

                        user.setName(etName.getText().toString());
                        user.setEmail(newEmail);
                        user.setPhoneNumber(etPhone.getText().toString());

                        db.collection("users")
                                .document(newEmail)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ProfileActivity.this, EventListActivity.class));
                                });
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Update failed", e));
        });
    }

    private void deleteUserProfile() {
        String email = user.getEmail();

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                    Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
