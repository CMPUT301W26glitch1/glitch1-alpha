package com.example.eventlotterysystemapp.ui.organizer;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

/**
 * The main menu for the organizer user.
 */
public class OrganizerMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        // Load data in real-time
        loadEvents();

        // Logout Button Logic
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> performLogout());

        // Create Event Button
        findViewById(R.id.btnCreateEvent).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            intent.putExtra("USER_EMAIL", loggedInUserEmail);
            startActivity(intent);
        });
    }

    /**
     * Method to logout the user
     */
    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        // Clears the stack to prevent back-navigation to the organizer menu
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Method to load the current organizer user's events from firestore
     */
    private void loadEvents() {
        if (loggedInUserEmail == null) return;

        FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("organizerId", loggedInUserEmail)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Failed to load events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    eventList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Event event = document.toObject(Event.class);
                            // Ensure your Event class has a setEventId method
                            event.setEventId(document.getId());
                            eventList.add(event);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    /**
     * Helper method to inject a test participant into the 'participants' sub-collection.
     * Uses the 'email' as the document ID to link with the 'users' collection.
     */
    private void addTestParticipant(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String testEmail = "john@example.com";

        // This links to the 'users' collection via email
        Participant testParticipant = new Participant(testEmail, "waitlist");

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(testEmail)
                .set(testParticipant)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Test participant injected!", Toast.LENGTH_SHORT).show()
                );
    }
}