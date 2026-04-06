package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.organizer.OrganizerMainActivity;

import android.widget.Toast;

// AdminDashboardActivity is the home screen for Admin users.
// It presents a menu of admin actions, each represented as a clickable CardView.
public class AdminDashboardActivity extends AppCompatActivity {
    private String adminEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        adminEmail = getIntent().getStringExtra("USER_EMAIL");

        // Bind each card to its view in activity_admin_dashboard.xml
        CardView cardManageEvents     = findViewById(R.id.cardManageEvents);
        CardView cardManageProfiles   = findViewById(R.id.cardManageProfiles);
        CardView cardManageImages     = findViewById(R.id.cardManageImages);
        CardView cardManageOrganizers = findViewById(R.id.cardManageOrganizers);
        CardView cardNotificationLogs = findViewById(R.id.cardNotificationLogs);
        CardView cardSwitchEntrant = findViewById(R.id.cardSwitchEntrant);
        CardView cardSwitchOrganizer = findViewById(R.id.cardSwitchOrganizer);

        // Clicking Manage Events launches the event list screen
        cardManageEvents.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageEventsActivity.class)));

        cardManageProfiles.setOnClickListener(v ->
            startActivity(new Intent(this, AdminManageProfilesActivity.class)));

        // The remaining cards are commented out until those activities are built

        cardManageImages.setOnClickListener(v ->
            startActivity(new Intent(this, AdminManageImagesActivity.class)));

        cardManageOrganizers.setOnClickListener(v ->
            startActivity(new Intent(this, AdminManageOrganizersActivity.class)));

        cardNotificationLogs.setOnClickListener(v ->
            startActivity(new Intent(this, AdminNotificationLogsActivity.class)));

        // Switch to Entrant Mode (EventListActivity)
        cardSwitchEntrant.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventListActivity.class);
            intent.putExtra("USER_EMAIL", adminEmail);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });

        // Switch to Organizer Mode (OrganizerMainActivity)
        cardSwitchOrganizer.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerMainActivity.class);
            intent.putExtra("USER_EMAIL", adminEmail);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });

        // press the back button to re-enter the dashboard after logging out.
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}