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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

            String eventId = doc.getId();
            String name = doc.getString("name");
            String location = doc.getString("location");

            java.util.Date dateTime = doc.getDate("dateTime");
            String dateStr = (dateTime != null)
                    ? new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dateTime)
                    : null;

            holder.tvName.setText(name != null ? name : "Unnamed Event");

            String dateLocation = (dateStr != null ? dateStr : "No date")
                    + " | " + (location != null ? location : "No location");
            holder.tvDateLocation.setText(dateLocation);

            Glide.with(holder.itemView.getContext()).clear(holder.eventPoster);
            String posterUrl = doc.getString("posterUrl");
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(posterUrl)
                        .into(holder.eventPoster);
            } else {
                holder.eventPoster.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminManageEventsActivity.this, AdminEventDetailActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventName", name);
                intent.putExtra("eventLocation", location);
                intent.putExtra("eventDescription", doc.getString("description"));
                intent.putExtra("eventCategory", doc.getString("category"));
                startActivity(intent);
            });

            holder.btnComments.setOnClickListener(v -> {
                Intent intent = new Intent(AdminManageEventsActivity.this, OrganizerEventCommentsActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("isAdminView", true);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDateLocation;
            ImageView eventPoster;
            Button btnComments;

            EventViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.eventName);
                tvDateLocation = itemView.findViewById(R.id.eventDesc);
                eventPoster = itemView.findViewById(R.id.eventPoster);
                btnComments = itemView.findViewById(R.id.btnComments);
            }
        }
    }
}