package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.eventlotterysystemapp.R;

// AdminDashboardActivity is the home screen for Admin users.
// It presents a menu of admin actions, each represented as a clickable CardView.
public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Bind each card to its view in activity_admin_dashboard.xml
        CardView cardManageEvents     = findViewById(R.id.cardManageEvents);
        CardView cardManageProfiles   = findViewById(R.id.cardManageProfiles);
        CardView cardManageImages     = findViewById(R.id.cardManageImages);
        CardView cardManageOrganizers = findViewById(R.id.cardManageOrganizers);
        CardView cardNotificationLogs = findViewById(R.id.cardNotificationLogs);

        // Clicking Manage Events launches the event list screen
        cardManageEvents.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageEventsActivity.class)));

        cardManageProfiles.setOnClickListener(v ->
            startActivity(new Intent(this, AdminManageProfilesActivity.class)));

        // The remaining cards are commented out until those activities are built
        /*
        cardManageImages.setOnClickListener(v ->
            startActivity(new Intent(this, AdminManageImagesActivity.class)));

        cardManageOrganizers.setOnClickListener(v ->
            startActivity(new Intent(this, AdminManageOrganizersActivity.class)));

        cardNotificationLogs.setOnClickListener(v ->
            startActivity(new Intent(this, AdminNotificationLogsActivity.class)));
        */

        // Logout button: navigates back to LoginActivity and clears the entire back stack.
        // FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK makes sure the admin cannot
        // press the back button to re-enter the dashboard after logging out.
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}