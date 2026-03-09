package com.example.eventlotterysystemapp.ui;

import static com.example.eventlotterysystemapp.ui.UiUtils.showNotification;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserController;

public class RegistrationActivity extends AppCompatActivity {
    Button registerButton;
    EditText username;
    EditText password;
    EditText email;
    Spinner roles;
    ArrayAdapter<String> rolesAdapter;
    UserController usersdb = new UserController(this);



    @Override
    protected void onCreate(Bundle savedInstanceState){
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

        String[] rolesList = new String[]{"Entrant", "Organizer", "Admin"};
        rolesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rolesList);
        roles.setAdapter(rolesAdapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                User user = new User(username.getText().toString(), password.getText().toString(), email.getText().toString(), roles.getSelectedItem().toString(), deviceId);
                usersdb.checkUser(user);
            }
        });

    }

    /**private void showNotification(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // This runs when they click OK
                    dialog.dismiss();
                })
                .show();
    }*/



}
