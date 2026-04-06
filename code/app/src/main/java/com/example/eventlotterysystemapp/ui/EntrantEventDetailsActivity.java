package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EntrantEventDetailsActivity extends AppCompatActivity {

    // Using the email from your login to match aarib's logic
    private String userEmail = "test3@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrantactivity_event_details);

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvBadge = findViewById(R.id.tvJoinedBadge);
        Button btnAction = findViewById(R.id.btnJoinLeave);
        Button btnHome = findViewById(R.id.btnHomepage);

        // 1. Get the data passed from the Adapter
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        String eventId = getIntent().getStringExtra("EVENT_ID");

        if (eventName != null) {
            tvName.setText(eventName);
        }

        if (eventId == null) {
            Toast.makeText(this, "Error: Could not load Event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // This is the specific path aarib showed in the screenshot
        DocumentReference participantRef = db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(userEmail);

        // --- 2. THE PERSISTENCE CHECK (Fixes the Refresh Bug) ---
        // We ask Firestore "Is this user already signed up?"
        participantRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // If the document exists, they are already on the waitlist
                btnAction.setText("Leave");
                tvBadge.setVisibility(View.VISIBLE);
            } else {
                // If not, show the Join button
                btnAction.setText("Join");
                tvBadge.setVisibility(View.GONE);
            }
        });

        // --- 3. THE CLICK LOGIC (Saves to Database) ---
        btnAction.setOnClickListener(v -> {
            if (btnAction.getText().toString().equalsIgnoreCase("Join")) {
                // JOINING: Create the map exactly like aarib's screenshot
                Map<String, Object> testParticipant = new HashMap<>();
                testParticipant.put("email", userEmail);
                testParticipant.put("status", "waitlist");

                participantRef.set(testParticipant).addOnSuccessListener(aVoid -> {
                    btnAction.setText("Leave");
                    tvBadge.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Added to Waitlist!", Toast.LENGTH_SHORT).show();
                });
            } else {
                // LEAVING: Delete the entry from the sub-collection
                participantRef.delete().addOnSuccessListener(aVoid -> {
                    btnAction.setText("Join");
                    tvBadge.setVisibility(View.GONE);
                    Toast.makeText(this, "Removed from Event", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Go back to the list
        btnHome.setOnClickListener(v -> finish());
    }
}