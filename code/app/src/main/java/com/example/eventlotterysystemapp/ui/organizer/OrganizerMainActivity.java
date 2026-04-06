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
import com.example.eventlotterysystemapp.ui.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The main menu for the organizer user
 */
public class OrganizerMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private String loggedInUserEmail;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        AccessibilityUtils.applyAccessibilityMode(this);

        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        // Retrieve the email passed from LoginActivity
        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnLogout = findViewById(R.id.btnLogout);

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        // Load data in real-time
        loadEvents();

        findViewById(R.id.btnCreateEvent).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            intent.putExtra("USER_EMAIL", loggedInUserEmail);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    // Simply go back to the Dashboard
                    finish();
                } else {
                    // Standard Logout for regular organizers
                    Intent intent = new Intent(OrganizerMainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Loads the current Organizer's created events from firestore database
     */
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
                                            //addTestParticipant(event.getEventId());
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                });
                    } else {
                        Toast.makeText(this, "User record not found for " + loggedInUserEmail, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Method used to add participants to an event to test if EventParticipantsActivity is functional
     * @param eventId Id of the event to add participants to
     */
    private void addTestParticipant(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // We only need email and status here
        String testEmail = "ent1@gmail.com";
        String testEmail1 = "org3@test.com";
        Participant testParticipant = new Participant(testEmail, "waitlist");
        Participant testParticipant1 = new Participant(testEmail1, "enrolled");

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(testEmail) // Unique ID
                .set(testParticipant)
                .addOnSuccessListener(aVoid -> {
                });

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(testEmail1)
                .set(testParticipant1)
                .addOnSuccessListener(aVoid -> {
                });
    }
}