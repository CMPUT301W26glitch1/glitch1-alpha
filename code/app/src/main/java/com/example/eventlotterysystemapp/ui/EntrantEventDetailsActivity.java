package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.Participant;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EntrantEventDetailsActivity extends AppCompatActivity {

    private ImageView ivPoster;
    private TextView tvName, tvDate, tvCapacity, tvDescription, tvStatus;
    private TextView tvCategory, tvLocation, tvRegDates, tvGeoWarning; // New Views
    private Button btnJoinLeave;

    private FirebaseFirestore db;
    private String eventId, userEmail;
    private ListenerRegistration statusListener, capacityListener;
    private boolean isFull = false;

    // Formatter for LocalDateTime fields
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - h:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrantactivity_event_details);

        // Ensure you have this utility or remove if not used
        // AccessibilityUtils.applyAccessibilityMode(this);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("EVENT_ID");
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (eventId == null || userEmail == null) {
            Toast.makeText(this, "Session expired or Event missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadEventData();
        setupStatusListener();
    }

    private void initViews() {
        ivPoster = findViewById(R.id.ivDetailedPoster);
        tvName = findViewById(R.id.tvDetailedEventName);
        tvDate = findViewById(R.id.tvDetailedEventDate);
        tvCapacity = findViewById(R.id.tvDetailedCapacity);
        tvDescription = findViewById(R.id.tvDetailedDescription);
        tvStatus = findViewById(R.id.tvUserCurrentStatus);

        // New view initializations
        tvCategory = findViewById(R.id.tvDetailedCategory);
        tvLocation = findViewById(R.id.tvDetailedLocation);
        tvRegDates = findViewById(R.id.tvDetailedRegDates);
        tvGeoWarning = findViewById(R.id.tvDetailedGeoWarning);

        btnJoinLeave = findViewById(R.id.btnJoinLeaveDetailed);

        btnJoinLeave.setOnClickListener(v -> handleJoinLeaveAction());
        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }

    private void loadEventData() {
        db.collection("events").document(eventId).get().addOnSuccessListener(doc -> {
            Event event = doc.toObject(Event.class);
            if (event != null) {
                event.setEventId(doc.getId());

                // Basic Info
                tvName.setText(event.getName());
                tvDescription.setText(event.getDescription());
                tvCategory.setText(event.getCategory());
                tvLocation.setText("📍 " + event.getLocation());

                // Event Date (using LocalDateTime)
                if (event.getDateTime() != null) {
                    tvDate.setText("📅 Event: " + event.getDateTime().format(dtFormatter));
                }

                // Registration Dates
                String regStart = (event.getRegStart() != null) ? event.getRegStart().format(dtFormatter) : "N/A";
                String regEnd = (event.getRegEnd() != null) ? event.getRegEnd().format(dtFormatter) : "N/A";
                tvRegDates.setText("Registration: " + regStart + " to " + regEnd);

                // Geolocation Warning
                tvGeoWarning.setVisibility(event.isGeolocationReq() ? View.VISIBLE : View.GONE);

                // Poster Image
                Glide.with(this)
                        .load(event.getPosterUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(ivPoster);

                monitorCapacity(event.getListLimit());
            }
        });
    }

    private void setupStatusListener() {
        DocumentReference pRef = db.collection("events").document(eventId)
                .collection("participants").document(userEmail);

        statusListener = pRef.addSnapshotListener((snapshot, e) -> {
            if (e != null || snapshot == null) return;

            if (snapshot.exists()) {
                String status = snapshot.getString("status");
                tvStatus.setText("Status: " + (status != null ? status.toUpperCase() : "NONE"));

                // If they are on waitlist, selected, or even cancelled, button should let them "Leave" or "Re-join"
                if ("waitlist".equalsIgnoreCase(status) || "selected".equalsIgnoreCase(status)) {
                    btnJoinLeave.setText("Leave Event");
                } else {
                    btnJoinLeave.setText("Join Event");
                }
            } else {
                tvStatus.setText("Status: NOT REGISTERED");
                btnJoinLeave.setText("Join Event");
            }
            refreshButtonState();
        });
    }

    private void monitorCapacity(int limit) {
        capacityListener = db.collection("events").document(eventId)
                .collection("participants")
                .whereEqualTo("status", "waitlist")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    int count = snapshot.size();
                    // Using 0 as the "No Limit" indicator based on your Event class
                    String limitStr = (limit <= 0) ? "No Limit" : String.valueOf(limit);
                    tvCapacity.setText("👥 Waitlist: " + count + " / " + limitStr);

                    isFull = (limit > 0 && count >= limit);
                    refreshButtonState();
                });
    }

    private void refreshButtonState() {
        if ("Join Event".equals(btnJoinLeave.getText().toString()) && isFull) {
            btnJoinLeave.setEnabled(false);
            btnJoinLeave.setText("Waitlist Full");
        } else {
            btnJoinLeave.setEnabled(true);
        }
    }

    private void handleJoinLeaveAction() {
        DocumentReference pRef = db.collection("events").document(eventId)
                .collection("participants").document(userEmail);

        if ("Join Event".equals(btnJoinLeave.getText().toString())) {
            Participant p = new Participant(userEmail, "waitlist");
            pRef.set(p).addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Joined waitlist!", Toast.LENGTH_SHORT).show());
        } else {
            // Standard behavior: allow user to remove themselves
            pRef.delete().addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Removed from event.", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statusListener != null) statusListener.remove();
        if (capacityListener != null) capacityListener.remove();
    }
}