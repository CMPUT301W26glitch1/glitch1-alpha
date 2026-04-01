package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventlotterysystemapp.R;

public class EntrantEventDetailsActivity extends AppCompatActivity {
    private boolean isJoined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrantactivity_event_details);

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvBadge = findViewById(R.id.tvJoinedBadge);
        Button btnAction = findViewById(R.id.btnJoinLeave);
        Button btnHome = findViewById(R.id.btnHomepage);

        // Receive the event name from the Adapter
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        if (eventName != null) {
            tvName.setText(eventName);
        }

        // Toggle logic for Join/Leave
        btnAction.setOnClickListener(v -> {
            isJoined = !isJoined;
            if (isJoined) {
                btnAction.setText("Leave");
                tvBadge.setVisibility(View.VISIBLE);
            } else {
                btnAction.setText("Join");
                tvBadge.setVisibility(View.GONE);
            }
        });

        // Go back to the list
        btnHome.setOnClickListener(v -> finish());
    }
}