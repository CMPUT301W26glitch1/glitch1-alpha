package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    ListView eventList;
    SearchView searchView;
    Spinner categorySpinner, availabilitySpinner;

    ArrayList<String> events;       // All events
    ArrayList<String> filteredEvents; // Filtered events
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Init views
        eventList = findViewById(R.id.eventList);
        searchView = findViewById(R.id.searchEvents);
        searchView.clearFocus();

        categorySpinner = findViewById(R.id.filterCategory);
        availabilitySpinner = findViewById(R.id.filterAvailability);

        // Sample events: format "EventName|Category|Availability"
        events = new ArrayList<>();
        events.add("Music Festival|Music|Open");
        events.add("Tech Conference|Science|Open");
        events.add("Startup Pitch Night|Arts|Closed");
        events.add("Art Exhibition|Arts|Open");
        events.add("Nature Walk|Nature|Closed");

        filteredEvents = new ArrayList<>(events);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredEvents);
        eventList.setAdapter(adapter);

        // Setup Category Spinner
        String[] categories = {"All", "Music", "Science", "Nature", "Arts"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);

        // Setup Availability Spinner
        String[] availability = {"All", "Open", "Closed"};
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availability);
        availabilitySpinner.setAdapter(availabilityAdapter);

        // Spinner listeners
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        categorySpinner.setOnItemSelectedListener(filterListener);
        availabilitySpinner.setOnItemSelectedListener(filterListener);

        // SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });
    }

    private void applyFilters() {
        String searchQuery = searchView.getQuery().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedAvailability = availabilitySpinner.getSelectedItem().toString();

        filteredEvents.clear();

        for (String event : events) {
            String[] parts = event.split("\\|"); // "EventName|Category|Availability"
            String name = parts[0];
            String category = parts[1];
            String availability = parts[2];

            boolean matchesSearch = name.toLowerCase().contains(searchQuery);
            boolean matchesCategory = selectedCategory.equals("All") || category.equals(selectedCategory);
            boolean matchesAvailability = selectedAvailability.equals("All") || availability.equals(selectedAvailability);

            if (matchesSearch && matchesCategory && matchesAvailability) {
                filteredEvents.add(name);
            }
        }

        adapter.notifyDataSetChanged();
    }
}