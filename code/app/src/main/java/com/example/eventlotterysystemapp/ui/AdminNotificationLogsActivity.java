package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerNotificationLogs;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> logList = new ArrayList<>();
    private NotificationLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification_logs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notification Logs");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        recyclerNotificationLogs = findViewById(R.id.recyclerNotificationLogs);
        recyclerNotificationLogs.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationLogAdapter();
        recyclerNotificationLogs.setAdapter(adapter);

        loadNotificationLogs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotificationLogs();
    }

    private void loadNotificationLogs() {
        db.collection("notificationLogs")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    logList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        logList.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading notification logs: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private class NotificationLogAdapter extends RecyclerView.Adapter<NotificationLogAdapter.NotificationLogViewHolder> {

        @Override
        public NotificationLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification_log, parent, false);
            return new NotificationLogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NotificationLogViewHolder holder, int position) {
            QueryDocumentSnapshot doc = logList.get(position);

            String eventName = doc.getString("eventName");
            String message = doc.getString("message");
            String sentBy = doc.getString("sentBy");
            String sentTo = doc.getString("sentTo");
            String timestamp = doc.getString("timestamp");

            holder.tvEventName.setText(eventName != null ? eventName : "No event name");
            holder.tvMessage.setText(message != null ? message : "No message");
            holder.tvSentBy.setText("Sent By: " + (sentBy != null ? sentBy : "Unknown"));
            holder.tvSentTo.setText("Sent To: " + (sentTo != null ? sentTo : "Unknown"));
            holder.tvTimestamp.setText(timestamp != null ? timestamp : "No timestamp");
        }

        @Override
        public int getItemCount() {
            return logList.size();
        }

        class NotificationLogViewHolder extends RecyclerView.ViewHolder {
            TextView tvEventName, tvMessage, tvSentBy, tvSentTo, tvTimestamp;

            NotificationLogViewHolder(View itemView) {
                super(itemView);
                tvEventName = itemView.findViewById(R.id.tvEventName);
                tvMessage = itemView.findViewById(R.id.tvMessage);
                tvSentBy = itemView.findViewById(R.id.tvSentBy);
                tvSentTo = itemView.findViewById(R.id.tvSentTo);
                tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            }
        }
    }
}