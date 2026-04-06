package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

}
