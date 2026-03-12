package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

// AdminManageEventsActivity fetches all events from Firestore and displays
// them in a scrollable list. Tapping an event opens its detail screen.
public class AdminManageEventsActivity extends AppCompatActivity {

    // RecyclerView is a scrollable list component.
    private RecyclerView recyclerEvents;

    // Firestore database instance
    private FirebaseFirestore db;

    // In-memory list of Firestore documents representing events.
    // QueryDocumentSnapshot is the type Firestore returns for each document in a collection.
    private List<QueryDocumentSnapshot> eventList = new ArrayList<>();

    // The adapter acts as the bridge between the eventList data and the RecyclerView UI
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_events);

        // Set up the toolbar with a back arrow that calls finish() to go back
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Bind the RecyclerView and set it to scroll vertically
        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));

        // Attach the adapter to the RecyclerView
        adapter = new EventAdapter();
        recyclerEvents.setAdapter(adapter);

        // Initial data load
        loadEvents();
    }

    // Calling loadEvents() ensures the list always reflects the current Firestore state.
    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    // loadEvents() performs an asynchronous read of the entire events collection.
    // On success it clears the old list, repopulates it, and notifies the adapter to redraw.
    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        eventList.add(doc);
                    }
                    // Tell the RecyclerView the data changed so it redraws the list
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading events: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // EventAdapter is an inner class that tells the RecyclerView how to:
    // 1. Create a new view holder (onCreateViewHolder)
    // 2. Bind data from eventList into that view holder (onBindViewHolder)
    // 3. How many items are in the list (getItemCount)
    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        // creates a new view for each row in the list using the item_event.xml layout
        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        }

        // onBindViewHolder is called for each visible row.
        // It reads the Firestore document at the given position and populates the TextViews.
        @Override
        public void onBindViewHolder(EventViewHolder holder, int position) {
            QueryDocumentSnapshot doc = eventList.get(position);

            // Read fields from the Firestore document
            String name     = doc.getString("name");
            String date     = doc.getString("date");
            String location = doc.getString("location");

            // if the field is null show a default string instead
            holder.tvName.setText(name != null ? name : "Unnamed Event");
            holder.tvDate.setText(date != null ? date : "No date");
            holder.tvLocation.setText(location != null ? location : "No location");

            // When a card is tapped, pass event data to AdminEventDetailActivity.
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminManageEventsActivity.this, AdminEventDetailActivity.class);
                intent.putExtra("eventId", doc.getId());       // Firestore document ID, needed to delete
                intent.putExtra("eventName", name);
                intent.putExtra("eventDate", date);
                intent.putExtra("eventLocation", location);
                intent.putExtra("eventDescription", doc.getString("description"));
                intent.putExtra("eventCategory", doc.getString("category"));
                startActivity(intent);
            });
        }

        // Returns the total number of items so RecyclerView knows when to stop rendering rows
        @Override
        public int getItemCount() {
            return eventList.size();
        }

        // EventViewHolder holds references to the TextViews inside each item_event.xml row.
        // Caching these references avoids calling findViewById() repeatedly on scroll.
        class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate, tvLocation;

            EventViewHolder(View itemView) {
                super(itemView);
                tvName     = itemView.findViewById(R.id.tvEventName);
                tvDate     = itemView.findViewById(R.id.tvEventDate);
                tvLocation = itemView.findViewById(R.id.tvEventLocation);
            }
        }
    }
}