package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.EntrantNotificationAdapter;
import com.example.eventlotterysystemapp.data.models.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

/**
 * Screen for Entrants to view lottery results and private event invitations.
 * Fulfills US 01.04.01, 01.04.02, 01.05.06, and 01.05.07.
 */
public class EntrantNotificationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EntrantNotificationAdapter adapter;
    private List<Notification> notifications;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_notification_list);
        AccessibilityUtils.applyAccessibilityMode(this);

        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rvNotifications);
        notifications = new ArrayList<>();

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        adapter = new EntrantNotificationAdapter(notifications, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadEntrantNotifications(userEmail);
    }

    private void loadEntrantNotifications(String email) {
        db.collection("notifications")
                .whereEqualTo("recipientEmail", email) // Match the field in your NotificationManager
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        android.util.Log.e("FIRESTORE", "Error: " + error.getMessage());
                        return;
                    }
                    if (value != null) {
                        notifications.clear();
                        for (DocumentSnapshot doc : value) {
                            Notification n = doc.toObject(Notification.class);
                            if (n != null) {
                                n.setId(doc.getId());
                                notifications.add(n);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}