package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

// AdminProfileDetailActivity displays the full details of a single user profile.
// It allows the admin to permanently delete the profile from Firestore.
public class AdminProfileDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    // Firestore document ID of the profile being viewed, needed to target the correct document for deletion
    private String profileId;
    private String profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_detail);

        db = FirebaseFirestore.getInstance();

        // set up toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // retrieve data passed from AdminManageProfilesActivity
        profileId = getIntent().getStringExtra("profileId");
        String name  = getIntent().getStringExtra("profileName");
        profileEmail = getIntent().getStringExtra("profileEmail");
        String email = profileEmail;
        String role  = getIntent().getStringExtra("profileRole");
        String phone = getIntent().getStringExtra("profilePhone");

        // bind TextViews to their views in activity_admin_profile_detail.xml
        TextView tvName  = findViewById(R.id.tvDetailName);
        TextView tvEmail = findViewById(R.id.tvDetailEmail);
        TextView tvRole  = findViewById(R.id.tvDetailRole);
        TextView tvPhone = findViewById(R.id.tvDetailPhone);
        Button btnDelete = findViewById(R.id.btnDeleteProfile);

        // if the field is null show a default string instead
        tvName.setText(name != null ? name : "No name");
        tvEmail.setText(email != null ? email : "No email");
        tvRole.setText(role != null ? role : "No role");
        tvPhone.setText(phone != null ? phone : "No phone number");

        // show a confirmation dialog before deleting to prevent accidental deletions
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Remove Profile")
                    .setMessage("Are you sure you want to remove \"" + name + "\"? This cannot be undone.")
                    .setPositiveButton("Remove", (dialog, which) -> deleteProfile())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // deletes the user document and all related data (participant entries, notifications)
    private void deleteProfile() {
        // 1. Delete notifications sent to this user
        db.collection("notifications")
                .whereEqualTo("recipientEmail", profileEmail)
                .get()
                .addOnSuccessListener(notifSnap -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : notifSnap.getDocuments()) {
                        doc.getReference().delete();
                    }

                    // 2. Remove this user from all event participant subcollections
                    db.collection("events").get().addOnSuccessListener(eventsSnap -> {
                        for (com.google.firebase.firestore.DocumentSnapshot eventDoc : eventsSnap.getDocuments()) {
                            eventDoc.getReference().collection("participants")
                                    .document(profileEmail)
                                    .delete();
                        }

                        // 3. Delete the user document itself
                        db.collection("users").document(profileId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Profile and related data removed", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    });
                });
    }
}
