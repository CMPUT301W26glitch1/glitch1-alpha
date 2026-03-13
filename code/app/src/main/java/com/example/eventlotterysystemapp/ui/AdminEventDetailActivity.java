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

    // deletes the event document from Firestore using the eventId
    // on success calls finish() which triggers onResume() in the list screen
    private void deleteEvent() {
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}