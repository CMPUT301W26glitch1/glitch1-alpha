package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

// AdminManageEventsActivity fetches all events from Firestore and displays
// them in a scrollable list. Tapping an event opens its detail screen.
public class AdminManageEventsActivity extends AppCompatActivity {

    // RecyclerView is an efficient scrollable list component.
    private RecyclerView recyclerEvents;

    // Firestore database instance
    private FirebaseFirestore db;

    // in-memory list of Firestore documents representing events
    private List<QueryDocumentSnapshot> eventList = new ArrayList<>();

    // the adapter to bridge the eventList data and the RecyclerView UI
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_events);
        AccessibilityUtils.applyAccessibilityMode(this);

        // set up toolbar with a back arrow that calls finish() to go back
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter();
        recyclerEvents.setAdapter(adapter);

        loadEvents();
    }

    // calling loadEvents() here ensures the list always reflects the current Firestore state.
    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    // performs an asynchronous read of the entire events collection.
    // on success clears the old list, repopulates it, and notifies the adapter to redraw.
    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        eventList.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading events: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // adapter that tells the RecyclerView how to create and bind each row
    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        // creates a new view for each row using the item_event.xml layout
        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_event, parent, false);
            return new EventViewHolder(view);
        }

        // binds Firestore data into the TextViews for each row
        @Override
        public void onBindViewHolder(EventViewHolder holder, int position) {

            QueryDocumentSnapshot doc = eventList.get(position);

            String name     = doc.getString("name");
            String location = doc.getString("location");

            // dateTime is stored as a Firestore Timestamp, not a String
            java.util.Date dateTime = doc.getDate("dateTime");
            String dateStr = (dateTime != null)
                    ? new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(dateTime)
                    : null;

            holder.tvName.setText(name != null ? name : "Unnamed Event");

            // combine date and location into eventDesc
            String dateLocation = (dateStr != null ? dateStr : "No date") + " | " + (location != null ? location : "No location");
            holder.tvDateLocation.setText(dateLocation);

            String posterUrl = doc.getString("posterUrl");
            ImageView poster = (ImageView) holder.itemView.findViewById(R.id.eventPoster);
            Glide.with(holder.itemView.getContext()).clear(poster); // clear any previous image first
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(posterUrl)
                        .into(poster);
            } else {
                poster.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // tapping a row passes event data to AdminEventDetailActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminManageEventsActivity.this, AdminEventDetailActivity.class);
                intent.putExtra("eventId", doc.getId());
                intent.putExtra("eventName", name);
                intent.putExtra("eventLocation", location);
                intent.putExtra("eventDescription", doc.getString("description"));
                intent.putExtra("eventCategory", doc.getString("category"));
                startActivity(intent);
            });
            holder.itemView.post(() -> AccessibilityUtils.applyToItemView(holder.itemView.getContext(), holder.itemView));
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        // holds references to the TextViews in each item_event.xml row
        class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDateLocation;

            EventViewHolder(View itemView) {
                super(itemView);
                tvName        = itemView.findViewById(R.id.eventName);
                tvDateLocation = itemView.findViewById(R.id.eventDesc);
            }
        }
    }
}