package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.util.Log;
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

        // Get email from the intent
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.rvHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Reuse your existing adapter!
        adapter = new EntrantEventAdapter(this, historyEvents, userEmail);
        recyclerView.setAdapter(adapter);

        loadJoinedHistory();
    }

    private void loadJoinedHistory() {
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e("HistoryError", "No user email provided");
            return;
        }

        db.collectionGroup("participants")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyEvents.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Safety check: ensure the parent path exists
                        if (doc.getReference().getParent() != null && doc.getReference().getParent().getParent() != null) {
                            doc.getReference().getParent().getParent().get()
                                    .addOnSuccessListener(eventDoc -> {
                                        if (eventDoc != null && eventDoc.exists()) {
                                            Event event = eventDoc.toObject(Event.class);
                                            if (event != null) {
                                                event.setEventId(eventDoc.getId());
                                                if (!historyEvents.contains(event)) {
                                                    historyEvents.add(event);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading history", Toast.LENGTH_SHORT).show();
                    Log.e("HistoryError", e.getMessage());
                });
    }
}