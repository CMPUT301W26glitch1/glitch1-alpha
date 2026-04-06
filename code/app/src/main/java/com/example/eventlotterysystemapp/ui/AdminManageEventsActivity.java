package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.organizer.OrganizerEventCommentsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerEvents;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> eventList = new ArrayList<>();
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_events);

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

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

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

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_event, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventViewHolder holder, int position) {

            QueryDocumentSnapshot doc = eventList.get(position);

            String name = doc.getString("name");
            String date = doc.getString("date");
            String location = doc.getString("location");

            // dateTime is stored as a Firestore Timestamp, not a String
            java.util.Date dateTime = doc.getDate("dateTime");
            String dateStr = (dateTime != null)
                    ? new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(dateTime)
                    : null;

            holder.tvName.setText(name != null ? name : "Unnamed Event");

            String dateLocation = (date != null ? date : "No date") + " | " +
                    (location != null ? location : "No location");
            holder.tvDateLocation.setText(dateLocation);

            String posterUrl = doc.getString("posterUrl");
            Glide.with(holder.itemView.getContext()).clear(holder.poster);
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(posterUrl)
                        .into(holder.poster);
            } else {
                holder.poster.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminManageEventsActivity.this, AdminEventDetailActivity.class);
                intent.putExtra("eventId", doc.getId());
                intent.putExtra("eventName", name);
                intent.putExtra("eventLocation", location);
                intent.putExtra("eventDescription", doc.getString("description"));
                intent.putExtra("eventCategory", doc.getString("category"));
                startActivity(intent);
            });

            holder.btnComments.setOnClickListener(v -> {
                Intent intent = new Intent(AdminManageEventsActivity.this, OrganizerEventCommentsActivity.class);
                intent.putExtra("eventId", doc.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDateLocation;
            ImageView poster;
            Button btnComments;

            EventViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.eventName);
                tvDateLocation = itemView.findViewById(R.id.eventDesc);
                poster = itemView.findViewById(R.id.eventPoster);
                btnComments = itemView.findViewById(R.id.btnComments);
            }
        }
    }
}