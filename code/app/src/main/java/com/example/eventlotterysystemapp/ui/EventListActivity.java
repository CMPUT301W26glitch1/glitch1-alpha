package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView eventRecyclerView;
    private SearchView searchView;
    private Button btnFilter;
    private FloatingActionButton fabNotifications, fabCamera;
    private TextView tvUserIcon;
    private android.widget.ImageView ivMainOptions;
    private ArrayList<Event> allEvents;
    private ArrayList<Event> filteredEvents;
    private EntrantEventAdapter adapter;

    // Fix: Define the constant here
    private static final int FILTER_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // 1. Initialize UI
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        searchView = findViewById(R.id.searchEvents);
        btnFilter = findViewById(R.id.btnFilter);
        fabNotifications = findViewById(R.id.fabNotifications);
        fabCamera = findViewById(R.id.fabCamera);
        tvUserIcon = findViewById(R.id.tvUserIcon);

        ivMainOptions = findViewById(R.id.ivMainOptions);

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Data setup
        allEvents = new ArrayList<>();
        loadMockData();

        filteredEvents = new ArrayList<>(allEvents);
        adapter = new EntrantEventAdapter(filteredEvents);
        eventRecyclerView.setAdapter(adapter);


        // 3. Click Listeners
        tvUserIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        ivMainOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Refresh List");
            popup.getMenu().add("Settings");
            popup.getMenu().add("Logout");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Refresh List")) {
                    loadMockData();
                    adapter.notifyDataSetChanged();
                    return true;
                } else if (item.getTitle().equals("Logout")) {
                    finish();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(this, FilterActivity.class);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        });

        fabNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Opening Notification...", Toast.LENGTH_SHORT).show());

        fabCamera.setOnClickListener(v ->
                Toast.makeText(this, "Camera/QR coming soon", Toast.LENGTH_SHORT).show());

        // 4. Search Logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBySearch(newText);
                return true;
            }
        });
    }

    // Fix: Move this OUTSIDE of the searchView listener
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                // A filter was applied
                String avail = data.getStringExtra("AVAIL");
                String interest = data.getStringExtra("INTEREST");
                applyAdvancedFilters(avail, interest);
            } else {
                // Reset or Back was pressed - Show EVERYTHING again
                filteredEvents.clear();
                filteredEvents.addAll(allEvents);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void applyAdvancedFilters(String avail, String interest) {
        filteredEvents.clear();

        // Convert filter words to lowercase to avoid "Music" vs "music" issues
        String filterAvail = avail.toLowerCase();
        String filterInterest = interest.toLowerCase();

        for (Event event : allEvents) {
            String eventName = (event.getName() != null) ? event.getName().toLowerCase() : "";

            // Rule 1: Match if "All" is selected OR the word is in the name
            // We also ignore the title "availability" or "interests"
            boolean matchAvail = avail.equals("All") || avail.equals("Availability") || avail.equalsIgnoreCase("open") || avail.equalsIgnoreCase("Full");
                    eventName.contains(filterAvail);

            boolean matchInterest = interest.equals("All") || interest.equals("Interests") ||
                    eventName.contains(filterInterest);

            if (matchAvail && matchInterest) {
                filteredEvents.add(event);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterBySearch(String text) {
        filteredEvents.clear();
        for (Event event : allEvents) {
            if (event.getName() != null && event.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredEvents.add(event);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadMockData() {
        // Fix: Use the empty constructor since that's what your Event.java has
        Event e1 = new Event(); e1.setName("Music Festival"); allEvents.add(e1);
        Event e2 = new Event(); e2.setName("Tech Conference"); allEvents.add(e2);
        Event e3 = new Event(); e3.setName("Art Exhibition"); allEvents.add(e3);
        Event e4 = new Event(); e4.setName("Nature Walk"); allEvents.add(e4);
    }
}