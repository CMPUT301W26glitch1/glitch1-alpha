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
                Intent intent = new Intent(OrganizerMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    /**
     * Loads the current Organizer's created events from firestore database
     */
    private void loadEvents() {
        if (loggedInUserEmail == null) return;

        // addSnapshotListener keeps the UI in sync with the database automatically
        FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("organizerId", loggedInUserEmail)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Failed to load events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    eventList.clear(); // Clear old data
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Event event = document.toObject(Event.class);
                            event.setEventId(document.getId());
                            eventList.add(event);
                            addTestParticipant(event.getEventId());
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh the list
                });
    }

    /**
     * Method used to add participants to an event to test if EventParticipantsActivity is functional
     * @param eventId Id of the event to add participants to
     */
    private void addTestParticipant(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // We only need email and status here
        String testEmail = "test1@gmail.com";
        Participant testParticipant = new Participant(testEmail, "waitlist");

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(testEmail) // Unique ID
                .set(testParticipant)
                .addOnSuccessListener(aVoid -> {
                });
    }
}