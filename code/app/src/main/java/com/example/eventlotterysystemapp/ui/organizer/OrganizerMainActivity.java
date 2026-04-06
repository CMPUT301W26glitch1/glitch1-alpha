package com.example.eventlotterysystemapp.ui.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.EventAdapter;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;
import com.example.eventlotterysystemapp.ui.LoginActivity;
import com.example.eventlotterysystemapp.ui.SettingsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * The main menu for the organizer user
 */
public class OrganizerMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private String loggedInUserEmail;
    private ActivityResultLauncher<Intent> settingsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        AccessibilityUtils.applyAccessibilityMode(this);

        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> { if (result.getResultCode() == RESULT_OK) recreate(); }
        );

        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        // Three-dot dropdown menu
        ImageView ivOptions = findViewById(R.id.ivOrganizerOptions);
        ivOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Settings");
            popup.getMenu().add("Logout");

            popup.setOnMenuItemClickListener(item -> {
                if ("Settings".equals(item.getTitle())) {
                    settingsLauncher.launch(new Intent(this, SettingsActivity.class));
                    return true;
                } else if ("Logout".equals(item.getTitle())) {
                    if (isAdmin) {
                        finish();
                    } else {
                        Intent intent = new Intent(OrganizerMainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            });
            popup.show();
        });

        loadEvents();

        findViewById(R.id.btnCreateEvent).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            intent.putExtra("USER_EMAIL", loggedInUserEmail);
            startActivity(intent);
        });
    }

    private void loadEvents() {
        if (loggedInUserEmail == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", loggedInUserEmail)
                .get()
                .addOnSuccessListener(userQuery -> {
                    if (!userQuery.isEmpty()) {
                        String userDocRefId = userQuery.getDocuments().get(0).getId();

                        db.collection("events")
                                .whereEqualTo("organizerId", userDocRefId)
                                .addSnapshotListener((value, error) -> {
                                    if (error != null) {
                                        Toast.makeText(this, "Load failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    eventList.clear();
                                    if (value != null) {
                                        for (QueryDocumentSnapshot document : value) {
                                            Event event = document.toObject(Event.class);
                                            event.setEventId(document.getId());
                                            eventList.add(event);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                });
                    } else {
                        Toast.makeText(this, "User record not found for " + loggedInUserEmail, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}