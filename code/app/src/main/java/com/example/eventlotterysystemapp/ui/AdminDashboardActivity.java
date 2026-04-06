package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.organizer.OrganizerMainActivity;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class AdminDashboardActivity extends AppCompatActivity {
    private String adminEmail;
    private ActivityResultLauncher<Intent> settingsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        AccessibilityUtils.applyAccessibilityMode(this);

        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> { if (result.getResultCode() == RESULT_OK) recreate(); }
        );

        adminEmail = getIntent().getStringExtra("USER_EMAIL");

        CardView cardManageEvents     = findViewById(R.id.cardManageEvents);
        CardView cardManageProfiles   = findViewById(R.id.cardManageProfiles);
        CardView cardManageImages     = findViewById(R.id.cardManageImages);
        CardView cardManageOrganizers = findViewById(R.id.cardManageOrganizers);
        CardView cardNotificationLogs = findViewById(R.id.cardNotificationLogs);
        CardView cardSwitchEntrant    = findViewById(R.id.cardSwitchEntrant);
        CardView cardSwitchOrganizer  = findViewById(R.id.cardSwitchOrganizer);
        CardView cardSettings         = findViewById(R.id.cardSettings);

        cardManageEvents.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageEventsActivity.class)));

        cardManageProfiles.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageProfilesActivity.class)));

        cardManageImages.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageImagesActivity.class)));

        cardManageOrganizers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageOrganizersActivity.class)));

        cardNotificationLogs.setOnClickListener(v ->
                startActivity(new Intent(this, AdminNotificationLogsActivity.class)));

        cardSwitchEntrant.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventListActivity.class);
            intent.putExtra("USER_EMAIL", adminEmail);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });

        cardSwitchOrganizer.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerMainActivity.class);
            intent.putExtra("USER_EMAIL", adminEmail);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v ->
                settingsLauncher.launch(new Intent(this, SettingsActivity.class)));

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
