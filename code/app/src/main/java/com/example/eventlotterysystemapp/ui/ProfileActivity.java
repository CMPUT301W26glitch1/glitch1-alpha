package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class ProfileActivity extends AppCompatActivity {
    EditText etName, etEmail, etPhone;
    Button btnSave;
    User user;
    FirebaseFirestore db;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AccessibilityUtils.applyAccessibilityMode(this);
        btnDelete = findViewById(R.id.btnDeleteProfile);

        btnSave = findViewById(R.id.btnSave);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        //Gets current user and preloads their info into text boxes
        user = UserSession.getUser();
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhoneNumber());
        btnDelete.setOnClickListener(v -> {
            // Always use a Dialog for deletion so it's not accidental!
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure? This will permanently remove your profile data.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteUserProfile();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        //save button ---> goes back to event list
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.setName(etName.getText().toString());
                user.setEmail(etEmail.getText().toString());
                user.setPhoneNumber(Integer.parseInt(etPhone.getText().toString()));

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .document(user.getName()) // using name as ID
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(ProfileActivity.this, EventListActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                            }
                        });

            }

        });



    }
    private void deleteUserProfile() {
        // Using user.getName() because that is your current document ID
        String documentId = user.getName();

        if (documentId != null && !documentId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();

                        // Redirect to Login and clear the backstack
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
