package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.NotificationManager;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class InviteCoorgActivity extends AppCompatActivity {

    private EditText searchCoorg;
    private Button btnSearchCoorg, btnBack;
    private ListView listCoorgResults;

    private FirebaseFirestore db;
    private ArrayList<String> userResults;
    private ArrayList<String> userIds;
    private ArrayAdapter<String> adapter;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_coorg);
        AccessibilityUtils.applyAccessibilityMode(this);

        eventId = getIntent().getStringExtra("EVENT_ID");

        searchCoorg = findViewById(R.id.searchCoorg);
        btnSearchCoorg = findViewById(R.id.btnSearchCoorg);
        listCoorgResults = findViewById(R.id.listCoorgResults);
        btnBack = findViewById(R.id.coorgBackBtn);

        db = FirebaseFirestore.getInstance();
        userResults = new ArrayList<>();
        userIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userResults);
        listCoorgResults.setAdapter(adapter);

        btnSearchCoorg.setOnClickListener(v -> searchUsers());

        listCoorgResults.setOnItemClickListener((parent, view, position, id) -> {
            String selectedInfo = userResults.get(position);
            String selectedUserId = userIds.get(position);

            // Extract email from "Name - Email" format
            String selectedEmail = selectedInfo;
            if (selectedInfo.contains(" - ")) {
                selectedEmail = selectedInfo.substring(selectedInfo.lastIndexOf(" - ") + 3);
            }

            sendCoorgInvite(selectedEmail, selectedUserId);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void searchUsers() {
        String query = searchCoorg.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) return;

        db.collection("users")
                .whereEqualTo("role", "Entrant")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    userResults.clear();
                    userIds.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");

                        String safeName = name != null ? name : "";
                        String safeEmail = email != null ? email : "";

                        if (safeName.toLowerCase().contains(query) || safeEmail.toLowerCase().contains(query)) {
                            userResults.add(safeName + " - " + safeEmail);
                            userIds.add(doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (userResults.isEmpty()) {
                        Toast.makeText(this, "No entrants found matching that query", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    private void sendCoorgInvite(String email, String userId) {
        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            if (!eventDoc.exists()) {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String eventName = eventDoc.getString("name");
            String finalEventName = (eventName != null) ? eventName : "an event";

            db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
                if (userDoc.exists()) {
                    Boolean optedOut = userDoc.getBoolean("notificationsOptedOut");
                    if (Boolean.TRUE.equals(optedOut)) {
                        Toast.makeText(this, "User has opted out of notifications", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Map<String, Object> notification = new HashMap<>();
                notification.put("recipientEmail", email);
                notification.put("recipientId", userId);
                notification.put("eventId", eventId);
                notification.put("message", "You have been invited to be a Co-Organizer for: " + finalEventName);
                notification.put("type", "COORG_INVITE");
                notification.put("status", "pending");
                notification.put("timestamp", Timestamp.now());

                db.collection("notifications")
                        .add(notification)
                        .addOnSuccessListener(documentReference -> {
                            documentReference.update("id", documentReference.getId());
                            Toast.makeText(this, "Invitation sent for " + finalEventName, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to send invitation", Toast.LENGTH_SHORT).show());
            });
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show());
    }
}