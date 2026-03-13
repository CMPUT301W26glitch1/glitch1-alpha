package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.eventlotterysystemapp.R;

public class AdminDashboardActivity extends AppCompatActivity {

    CardView cardManageEvents;
    CardView cardManageProfiles;
    CardView cardManageImages;
    CardView cardManageOrganizers;
    CardView cardNotificationLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Bind views
        cardManageEvents = findViewById(R.id.cardManageEvents);
        cardManageProfiles = findViewById(R.id.cardManageProfiles);
        cardManageImages = findViewById(R.id.cardManageImages);
        cardManageOrganizers = findViewById(R.id.cardManageOrganizers);
        cardNotificationLogs = findViewById(R.id.cardNotificationLogs);

        // Open Notification Logs screen
        cardNotificationLogs.setOnClickListener(v ->
                startActivity(new Intent(this, AdminNotificationLogsActivity.class)));
    }
}