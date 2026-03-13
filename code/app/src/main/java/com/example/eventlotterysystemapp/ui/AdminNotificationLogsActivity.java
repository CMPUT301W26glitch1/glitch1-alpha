package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerNotifications;
    private FirebaseFirestore db;
    private final List<String> notifications = new ArrayList<>();
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification_logs);

        recyclerNotifications = findViewById(R.id.recyclerNotifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(notifications);
        recyclerNotifications.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        db.collection("notificationLogs")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    notifications.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {

                        String message = doc.getString("message");
                        String sentBy = doc.getString("sentBy");
                        String sentTo = doc.getString("sentTo");
                        String eventName = doc.getString("eventName");
                        String timestamp = doc.getString("timestamp");

                        String logText =
                                "Message: " + (message != null ? message : "") +
                                        "\nSent By: " + (sentBy != null ? sentBy : "") +
                                        "\nSent To: " + (sentTo != null && !sentTo.trim().isEmpty() ? sentTo : "Entrants") +
                                        "\nEvent: " + (eventName != null ? eventName : "") +
                                        "\nTime: " + (timestamp != null ? timestamp : "");

                        notifications.add(logText);
                    }

                    if (notifications.isEmpty()) {
                        notifications.add("No notification logs yet.");
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    notifications.clear();
                    notifications.add("Failed to load notification logs.");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Failed to load notification logs", Toast.LENGTH_SHORT).show();
                });
    }
}