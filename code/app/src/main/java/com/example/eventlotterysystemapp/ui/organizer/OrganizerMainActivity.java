package com.example.eventlotterysystemapp.ui.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.EventAdapter;
import com.example.eventlotterysystemapp.data.models.Participant;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;
import com.example.eventlotterysystemapp.ui.LoginActivity;
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
    private String loggedInUserId;
    private String loggedInUserName;

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        AccessibilityUtils.applyAccessibilityMode(this);

        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        // Get logged-in email
        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnLogout = findViewById(R.id.btnLogout);

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        loadEvents();

        // Create Event button
        findViewById(R.id.btnCreateEvent).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            intent.putExtra("USER_EMAIL", loggedInUserEmail);
            startActivity(intent);
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            if (isAdmin) {
                finish();
            } else {
                Intent intent = new Intent(OrganizerMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    /**
     * Load organizer's events from Firestore
     */
    private void loadEvents() {
        if (loggedInUserEmail == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: Get organizer user document
        db.collection("users")
                .whereEqualTo("email", loggedInUserEmail)
                .get()
                .addOnSuccessListener(userQuery -> {

                    if (!userQuery.isEmpty()) {

                        // Get user ID + NAME
                        loggedInUserId = userQuery.getDocuments().get(0).getId();
                        loggedInUserName = userQuery.getDocuments().get(0).getString("name");

                        // 🚨 THIS LINE IS VERY IMPORTANT
                        adapter.setCurrentUserInfo(loggedInUserId, loggedInUserName);

                        // Step 2: Load events created by this organizer
                        db.collection("events")
                                .whereEqualTo("organizerId", loggedInUserId)
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
                        Toast.makeText(this, "User not found: " + loggedInUserEmail, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Optional test helper (unchanged)
     */
    private void addTestParticipant(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String testEmail = "ent1@gmail.com";
        String testEmail1 = "org3@test.com";

        Participant p1 = new Participant(testEmail, "waitlist");
        Participant p2 = new Participant(testEmail1, "enrolled");

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(testEmail)
                .set(p1);

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(testEmail1)
                .set(p2);
    }
}