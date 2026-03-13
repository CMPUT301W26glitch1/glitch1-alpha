package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.EventController;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    ListView eventList;
    SearchView searchView;

    ArrayList<String> events;
    ArrayAdapter<String> adapter;
    EventController eventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        eventList = findViewById(R.id.eventList);
        searchView = findViewById(R.id.searchEvents);
        searchView.setIconified(false);
        searchView.clearFocus();
        events = new ArrayList<>();

// temporary test events
        events.add("Music Festival");
        events.add("Tech Conference");
        events.add("Startup Pitch Night");
        events.add("Art Exhibition");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);
        eventList.setAdapter(adapter);

// Firestore controller (leave this if you want real events later)
      /*  eventController = new EventController(this);
        eventController.getAllEvents(events, adapter);*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            adapter.getFilter().filter(query);
                return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}