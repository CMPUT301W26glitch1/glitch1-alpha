package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

// AdminEventDetailActivity displays the full details of a single event.
// It fetches the event document directly from Firestore to get all fields
// including dateTime, regStart, regEnd, geolocationReq, and posterUrl.
public class AdminEventDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    // Firestore document ID needed to fetch and delete the correct event
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_detail);
        AccessibilityUtils.applyAccessibilityMode(this);

        db = FirebaseFirestore.getInstance();

        // set up toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // get the event document ID passed from AdminManageEventsActivity
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // fetch the full event document from Firestore to get all fields
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(this::populateUI)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // show confirmation dialog before deleting
        Button btnDelete = findViewById(R.id.btnDeleteEvent);
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event? This cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteEvent())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // populateUI() fills all the TextViews and the poster ImageView
    // using the document snapshot fetched directly from Firestore
    private void populateUI(DocumentSnapshot doc) {
        TextView tvName        = findViewById(R.id.tvDetailName);
        TextView tvCategory    = findViewById(R.id.tvDetailCategory);
        TextView tvLocation    = findViewById(R.id.tvDetailLocation);
        TextView tvDateTime    = findViewById(R.id.tvDetailDateTime);
        TextView tvRegStart    = findViewById(R.id.tvDetailRegStart);
        TextView tvRegEnd      = findViewById(R.id.tvDetailRegEnd);
        TextView tvGeo         = findViewById(R.id.tvDetailGeo);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        ImageView ivPoster     = findViewById(R.id.ivEventPoster);

        tvName.setText(doc.getString("name") != null ? doc.getString("name") : "Unnamed Event");
        tvCategory.setText(doc.getString("category") != null ? doc.getString("category") : "No category");
        tvLocation.setText(doc.getString("location") != null ? doc.getString("location") : "No location");
        tvDescription.setText(doc.getString("description") != null ? doc.getString("description") : "No description");

        // dateTime, regStart, regEnd are stored as Firestore Timestamps
        // getDate() converts them to a readable string
        tvDateTime.setText(doc.getDate("dateTime") != null ? doc.getDate("dateTime").toString() : "No date set");
        tvRegStart.setText(doc.getDate("regStart") != null ? doc.getDate("regStart").toString() : "No start date set");
        tvRegEnd.setText(doc.getDate("regEnd") != null ? doc.getDate("regEnd").toString() : "No end date set");

        // geolocationReq is a boolean field
        Boolean geo = doc.getBoolean("geolocationReq");
        tvGeo.setText(geo != null && geo ? "Yes" : "No");

        // load the poster image using Glide if a URL exists
        String posterUrl = doc.getString("posterUrl");
        android.util.Log.d("POSTER", "URL: " + posterUrl);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(getApplicationContext()).load(posterUrl).into(ivPoster);        }
    }

    // deletes the event and all related data (participants, notifications, poster)
    private void deleteEvent() {
        // 1. Delete the poster image from Firebase Storage (if it exists)
        try {
            FirebaseStorage.getInstance().getReference("event_posters/" + eventId + ".jpg").delete();
        } catch (Exception ignored) {}
        try {
            FirebaseStorage.getInstance().getReference("posters/" + eventId).delete();
        } catch (Exception ignored) {}

        // 2. Delete all participants in the subcollection
        db.collection("events").document(eventId).collection("participants")
                .get()
                .addOnSuccessListener(participantSnap -> {
                    for (DocumentSnapshot doc : participantSnap.getDocuments()) {
                        doc.getReference().delete();
                    }

                    // 3. Delete all notifications linked to this event
                    db.collection("notifications")
                            .whereEqualTo("eventId", eventId)
                            .get()
                            .addOnSuccessListener(notifSnap -> {
                                for (DocumentSnapshot doc : notifSnap.getDocuments()) {
                                    doc.getReference().delete();
                                }

                                // 4. Delete the event document itself
                                db.collection("events").document(eventId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Event and related data deleted", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            });
                });
    }
}