package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.eventlotterysystemapp.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        CardView cardManageEvents = findViewById(R.id.cardManageEvents);
        CardView cardManageProfiles = findViewById(R.id.cardManageProfiles);
        CardView cardManageImages = findViewById(R.id.cardManageImages);
        CardView cardManageOrganizers = findViewById(R.id.cardManageOrganizers);
        CardView cardNotificationLogs = findViewById(R.id.cardNotificationLogs);

        // Manage Events
        cardManageEvents.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageEventsActivity.class)));

        // Manage Profiles
        cardManageProfiles.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageProfilesActivity.class)));

        // Manage Images
        cardManageImages.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageImagesActivity.class)));

        // Manage Organizers
        cardManageOrganizers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageOrganizersActivity.class)));

        // Notification Logs
        cardNotificationLogs.setOnClickListener(v ->
                startActivity(new Intent(this, AdminNotificationLogsActivity.class)));

        // Logout
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}