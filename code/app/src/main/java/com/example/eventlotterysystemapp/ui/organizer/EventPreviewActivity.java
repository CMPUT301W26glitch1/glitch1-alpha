package com.example.eventlotterysystemapp.ui.organizer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;

public class EventPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_preview);

        // 🔥 Enable default Android back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Preview");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // 🔥 THIS handles the back arrow click
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}