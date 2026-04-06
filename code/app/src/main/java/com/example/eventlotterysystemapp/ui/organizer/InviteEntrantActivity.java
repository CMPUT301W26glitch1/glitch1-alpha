package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class InviteEntrantActivity extends AppCompatActivity {

    private EditText searchEntrant;
    private Button btnSearchEntrant;
    private ListView listEntrants;

    private Button btnBack;
    private FirebaseFirestore db;
    private ArrayList<String> entrantResults;
    private ArrayList<String> entrantIds;
    private ArrayAdapter<String> adapter;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_entrant);
        AccessibilityUtils.applyAccessibilityMode(this);

        eventId = getIntent().getStringExtra("EVENT_ID");

        searchEntrant = findViewById(R.id.searchEntrant);
        btnSearchEntrant = findViewById(R.id.btnSearchEntrant);
        listEntrants = findViewById(R.id.listEntrants);
        btnBack = findViewById(R.id.backBtn);

        db = FirebaseFirestore.getInstance();
        entrantResults = new ArrayList<>();
        entrantIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                entrantResults
        );
        listEntrants.setAdapter(adapter);

        btnSearchEntrant.setOnClickListener(v -> searchUsers());

        listEntrants.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEmail = entrantResults.get(position);
            String selectedUserId = entrantIds.get(position);

            if (selectedEmail.contains(" - ")) {
                selectedEmail = selectedEmail.substring(selectedEmail.lastIndexOf(" - ") + 3);
            }

            inviteUserToWaitlist(selectedEmail, selectedUserId);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void searchUsers() {
        String query = searchEntrant.getText().toString().trim().toLowerCase();

        db.collection("users")
                .whereEqualTo("role", "Entrant")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    entrantResults.clear();
                    entrantIds.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        Object phone = doc.get("phoneNumber");

                        String safeName = name != null ? name : "";
                        String safeEmail = email != null ? email : "";
                        String safePhone = phone != null ? phone.toString() : "";

                        if (safeName.toLowerCase().contains(query) || safeEmail.toLowerCase().contains(query) || safePhone.toLowerCase().contains(query)) {
                            entrantResults.add(safeName + " - " + safeEmail);
                            entrantIds.add(doc.getId());
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (entrantResults.isEmpty()) {
                        Toast.makeText(this, "No matching entrants found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Search failed", Toast.LENGTH_SHORT).show());
    }

    private void inviteUserToWaitlist(String email, String userId) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (!userSnapshot.isEmpty()) {
                        Boolean optedOut = userSnapshot.getDocuments().get(0).getBoolean("notificationsOptedOut");
                        if (optedOut != null && optedOut) {
                            Toast.makeText(this, "User has opted out of notifications", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    db.collection("events")
                            .document(eventId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                String eventName = documentSnapshot.getString("name");
                                if (eventName == null || eventName.isEmpty()) {
                                    eventName = "Private Event";
                                }

                                Map<String, Object> notification = new HashMap<>();
                                notification.put("recipientEmail", email);
                                notification.put("recipientId", userId);
                                notification.put("eventId", eventId);
                                notification.put("eventName", eventName);
                                notification.put("message", "You have been invited to join the waiting list for a private event.");
                                notification.put("type", "PRIVATE_INVITE");
                                notification.put("status", "pending");
                                notification.put("timestamp", Timestamp.now());

                                db.collection("notifications")
                                        .add(notification)
                                        .addOnSuccessListener(documentReference -> {
                                            documentReference.update("id", documentReference.getId());
                                            Toast.makeText(this, "Private invitation sent", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Failed to send invitation", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to load event info", Toast.LENGTH_SHORT).show());
                });
    }
}