package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private TextView EventName;
    private EditText NotificationMessage;
    private Spinner spinnerSendTo;
    private Button sendNotificationBtn;
    private Button backBtn;

    private FirebaseFirestore db;
    private ArrayList<String> recipientEmails;
    private String eventId;
    private String eventName = "Event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        eventId = getIntent().getStringExtra("EVENT_ID");

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EventName = findViewById(R.id.eventName);
        NotificationMessage = findViewById(R.id.notificationMessage);
        spinnerSendTo = findViewById(R.id.spinnerSendTo);
        sendNotificationBtn = findViewById(R.id.sendNotificationBtn);
        backBtn = findViewById(R.id.BtnBack);

        db = FirebaseFirestore.getInstance();
        recipientEmails = new ArrayList<>();

        String[] options = {"Select one", "Waiting", "Selected", "Cancelled"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSendTo.setAdapter(spinnerAdapter);

        loadEventName();

        sendNotificationBtn.setOnClickListener(v -> sendNotifications());
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadEventName() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("name");
                    if (name != null && !name.isEmpty()) {
                        eventName = name;
                        EventName.setText(eventName);
                    }
                });
    }

    private void sendNotifications() {
        String selectedGroup = spinnerSendTo.getSelectedItem().toString();
        String message = NotificationMessage.getText().toString().trim();

        if (selectedGroup.equals("Select one")) {
            Toast.makeText(this, "Please select a group", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a notification message", Toast.LENGTH_SHORT).show();
            return;
        }

        String statusToMatch = selectedGroup.toLowerCase();
        recipientEmails.clear();

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .whereEqualTo("status", statusToMatch)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String email = doc.getId(); // safer than doc.getString("email")
                        if (email != null && !email.isEmpty()) {
                            recipientEmails.add(email);
                        }
                    }

                    if (recipientEmails.isEmpty()) {
                        Toast.makeText(this, "No recipients found in " + selectedGroup, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String currentTime = new SimpleDateFormat("MMM dd hh:mm", Locale.getDefault()).format(new Date());

                    for (String email : recipientEmails) {
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("recipientEmail", email);
                        notification.put("eventId", eventId);
                        notification.put("eventName", eventName);
                        notification.put("message", message);
                        notification.put("type", selectedGroup.toLowerCase());
                        notification.put("timestamp", Timestamp.now());

                        db.collection("notifications").add(notification);
                    }

                    Map<String, Object> log = new HashMap<>();
                    log.put("message", message);
                    log.put("sentBy", "Organizer");
                    log.put("sentTo", selectedGroup);
                    log.put("eventName", eventName);
                    log.put("timestamp", currentTime);

                    db.collection("notificationLogs")
                            .add(log)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this, "Notification sent successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to log notification", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load recipients", Toast.LENGTH_SHORT).show());
    }
}