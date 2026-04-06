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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class ProfileActivity extends AppCompatActivity {
    EditText etName, etEmail, etPhone;
    Button btnSave;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AccessibilityUtils.applyAccessibilityMode(this);

        btnSave = findViewById(R.id.btnSave);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        //Gets current user and preloads their info into text boxes
        user = UserSession.getUser();
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhoneNumber());

        //save button ---> goes back to event list
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .whereEqualTo("email",originalEmail)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {

                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                doc.getReference().delete();
                            }
                            // update user object
                            user.setName(etName.getText().toString());
                            user.setEmail(newEmail);
                            user.setPhoneNumber(etPhone.getText().toString());

                            // now create new document
                            db.collection("users")
                                    .document(newEmail)
                                    .set(user)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileActivity.this, EventListActivity.class));
                                    });

                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Delete failed", e);
                        });

            }

        });



    }

}
