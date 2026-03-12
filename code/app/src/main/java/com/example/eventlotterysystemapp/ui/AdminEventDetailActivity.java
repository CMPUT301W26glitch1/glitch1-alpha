package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

// AdminEventDetailActivity displays the full details of a single event.
// It also allows the admin to permanently delete the event from Firestore.
public class AdminEventDetailActivity extends AppCompatActivity {

    // Firestore database instance
    private FirebaseFirestore db;

    // The Firestore document ID of the event being viewed.
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_detail);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up the toolbar with a back arrow that calls finish() to return to the event list
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Retrieve the data passed from AdminManageEventsActivity.
        eventId = getIntent().getStringExtra("eventId");
        String name        = getIntent().getStringExtra("eventName");
        String date        = getIntent().getStringExtra("eventDate");
        String location    = getIntent().getStringExtra("eventLocation");
        String description = getIntent().getStringExtra("eventDescription");
        String category    = getIntent().getStringExtra("eventCategory");

        // Bind TextViews to their views in activity_admin_event_detail.xml
        TextView tvName        = findViewById(R.id.tvDetailName);
        TextView tvDate        = findViewById(R.id.tvDetailDate);
        TextView tvLocation    = findViewById(R.id.tvDetailLocation);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        TextView tvCategory    = findViewById(R.id.tvDetailCategory);
        Button   btnDelete     = findViewById(R.id.btnDeleteEvent);

        // Populate the TextViews with the event data, with fallback strings for null fields
        tvName.setText(name != null ? name : "Unnamed Event");
        tvDate.setText(date != null ? date : "No date");
        tvLocation.setText(location != null ? location : "No location");
        tvDescription.setText(description != null ? description : "No description");
        tvCategory.setText(category != null ? category : "No category");

        // Show a confirmation dialog before deleting to prevent accidental deletions
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete \"" + name + "\"? This cannot be undone.")
                    // Positive button triggers the actual deletion
                    .setPositiveButton("Delete", (dialog, which) -> deleteEvent())
                    // Negative button dismisses the dialog with no action (null listener)
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // deleteEvent() performs an asynchronous Firestore document deletion.
    // It targets the specific document using the eventId passed from the list screen.
    private void deleteEvent() {
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                    // finish() pops this activity off the back stack, returning to the event list.
                    // onResume() in AdminManageEventsActivity will then trigger a fresh Firestore fetch.
                    finish();
                })
                // If the deletion fails (e.g. network error or permissions issue), show the error
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}