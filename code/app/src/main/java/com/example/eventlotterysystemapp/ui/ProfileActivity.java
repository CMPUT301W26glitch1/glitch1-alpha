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

public class ProfileActivity extends AppCompatActivity {
    EditText etName, etEmail, etPhone;
    Button btnSave;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnSave = findViewById(R.id.btnSave);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        user = UserSession.getUser();
        //save button ---> goes back to event list
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.setName(etName.getText().toString());
                user.setEmail(etName.getText().toString());
                user.setPhoneNumber(etName.getText().toString());


                Intent intent = new Intent(ProfileActivity.this, EventListActivity.class);
                startActivity(intent);
            }

        });



    }

}
