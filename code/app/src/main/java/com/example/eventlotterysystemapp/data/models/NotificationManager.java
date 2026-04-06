package com.example.eventlotterysystemapp.data.models;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationManager {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Sends a notification to a specific user.
     * Updated to include recipientEmail and status for actionable items.
     */
    public void sendNotification(String recipientId, String recipientEmail, String message, String type, String eventId) {

        db.collection("users").document(recipientId).get().addOnSuccessListener(doc -> {
            Boolean notificationsEnabled = doc.contains("notificationsEnabled") ?
                    doc.getBoolean("notificationsEnabled") : true;

            if (Boolean.FALSE.equals(notificationsEnabled)) return;

            Map<String, Object> notif = new HashMap<>();
            notif.put("recipientId", recipientId);
            notif.put("recipientEmail", recipientEmail);
            notif.put("message", message);
            notif.put("type", type);
            notif.put("eventId", eventId);
            notif.put("status", "pending");
            notif.put("timestamp", FieldValue.serverTimestamp());

            db.collection("notifications").add(notif);
        });
    }
}