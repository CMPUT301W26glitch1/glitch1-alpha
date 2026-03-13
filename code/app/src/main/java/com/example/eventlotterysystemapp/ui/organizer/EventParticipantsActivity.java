package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.ParticipantPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * View to show the list of participants for an event.
 * Split into three tabs for waitlist, selected and cancelled participants
 */
public class EventParticipantsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        String eventId = getIntent().getStringExtra("EVENT_ID");

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // The adapter for the 3 tabs
        ParticipantPagerAdapter adapter = new ParticipantPagerAdapter(this, eventId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch(position) {
                case 0: tab.setText("Waitlist"); break;
                case 1: tab.setText("Selected"); break;
                case 2: tab.setText("Cancelled"); break;
            }
        }).attach();
    }
}