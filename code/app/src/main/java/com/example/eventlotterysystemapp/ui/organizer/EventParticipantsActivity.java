package com.example.eventlotterysystemapp.ui.organizer;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.ParticipantPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventParticipantsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        String eventId = getIntent().getStringExtra("EVENT_ID");

        // Initialize Buttons
        Button btnReturn = findViewById(R.id.btnReturn);
        Button btnNotifyTop = findViewById(R.id.btnNotifyTop);

        // Simple finish() to go back to OrganizerMainActivity
        btnReturn.setOnClickListener(v -> finish());

        btnNotifyTop.setOnClickListener(v -> {
            Intent intent = new Intent(EventParticipantsActivity.this, NotificationActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ParticipantPagerAdapter adapter = new ParticipantPagerAdapter(this, eventId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch(position) {
                case 0: tab.setText("Waitlist"); break;
                case 1: tab.setText("Selected"); break;
                case 2: tab.setText("Cancelled"); break;
                case 3: tab.setText("Enrolled"); break;
            }
        }).attach();
    }
}