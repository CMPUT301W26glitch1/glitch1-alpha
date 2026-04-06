package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.EntrantEventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class EventHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EntrantEventAdapter adapter;
    private ArrayList<Event> historyEvents = new ArrayList<>();
    private FirebaseFirestore db;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail != null) {
            userEmail = userEmail.toLowerCase().trim(); // Clean it!
        }
        db = FirebaseFirestore.getInstance();

        // 1. Back Button Fix
        ImageButton btnBack = findViewById(R.id.btnBackToProfile);
        btnBack.setOnClickListener(v -> finish());

        // 2. Setup List
        recyclerView = findViewById(R.id.rvHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantEventAdapter(this, historyEvents, userEmail);
        recyclerView.setAdapter(adapter);

        loadJoinedHistory();
    }

    private void loadJoinedHistory() {
        if (userEmail == null) return;

        db.collectionGroup("participants")
                .whereEqualTo("email", userEmail.toLowerCase().trim())
                .get()
                .addOnSuccessListener(querySnap -> {
                    // Clear the list once at the start
                    historyEvents.clear();

                    if (querySnap.isEmpty()) {
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    // Count how many we need to find so we know when we are "done"
                    int totalToLoad = querySnap.size();
                    final int[] loadedCount = {0};

                    for (QueryDocumentSnapshot doc : querySnap) {
                        // Navigate from /events/{id}/participants/{email} up to /events/{id}
                        doc.getReference().getParent().getParent().get()
                                .addOnSuccessListener(eventDoc -> {
                                    loadedCount[0]++; // Increment every time a doc comes back

                                    if (eventDoc.exists()) {
                                        Event event = eventDoc.toObject(Event.class);
                                        if (event != null) {
                                            event.setEventId(eventDoc.getId());
                                            // Only add if not already there (prevents duplicates)
                                            if (!historyEvents.contains(event)) {
                                                historyEvents.add(event);
                                            }
                                        }
                                    }

                                    // ONLY notify the adapter when the LAST document is loaded
                                    // This prevents the "Exit Crash" from too many UI updates
                                    if (loadedCount[0] == totalToLoad) {
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    loadedCount[0]++;
                                    if (loadedCount[0] == totalToLoad) {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading history", Toast.LENGTH_SHORT).show();
                });
    }
}