package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.EntrantEventAdapter;
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

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
    private FirebaseFirestore db;
    private String loggedInUserEmail;
    private ActivityResultLauncher<Intent> settingsLauncher;

    // Track current search and filter state so they can be combined
    private String currentSearchQuery = "";
    private String currentFilterAvail = null;
    private String currentFilterInterest = null;

    private static final int FILTER_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        AccessibilityUtils.applyAccessibilityMode(this);

        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        recreate();
                    }
                }
        );

        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");

        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        searchView = findViewById(R.id.searchEvents);
        btnFilter = findViewById(R.id.btnFilter);
        fabNotifications = findViewById(R.id.fabNotifications);
        fabCamera = findViewById(R.id.fabCamera);
        tvUserIcon = findViewById(R.id.tvUserIcon);
        ivMainOptions = findViewById(R.id.ivMainOptions);

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(EventListActivity.this, EntrantNotificationListActivity.class);
            intent.putExtra("USER_EMAIL", loggedInUserEmail);
            startActivity(intent);
        });

        fabCamera.setOnClickListener(v -> {
            Intent intent = new Intent(EventListActivity.this, QRScannerActivity.class);
            startActivity(intent);
        });

        allEvents = new ArrayList<>();
        filteredEvents = new ArrayList<>();

        adapter = new EntrantEventAdapter(this, filteredEvents, loggedInUserEmail);
        eventRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        startFirebaseListener();

        tvUserIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        ivMainOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Refresh List");
            popup.getMenu().add("Lottery Info");
            popup.getMenu().add("History");
            popup.getMenu().add("Settings");
            popup.getMenu().add("Logout");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Refresh List")) {
                    startFirebaseListener();
                    return true;
                } else if (item.getTitle().equals("Lottery Info")) {
                    startActivity(new Intent(EventListActivity.this, LotteryInfoActivity.class));
                    return true;
                } else if (item.getTitle().equals("History")) {
                    Intent intent = new Intent(EventListActivity.this, EventHistoryActivity.class);
                    intent.putExtra("USER_EMAIL", loggedInUserEmail);
                    startActivity(intent);
                    return true;

                } else if (item.getTitle().equals("Settings")) {
                    settingsLauncher.launch(new Intent(EventListActivity.this, SettingsActivity.class));
                    return true;
                } else if (item.getTitle().equals("Logout")) {
                    if (isAdmin) {
                        finish();
                        Toast.makeText(this, "Returning to Admin Panel", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = (newText == null) ? "" : newText.trim();
                applyAllFilters();
                return true;
            }
        });
    }

    private void startFirebaseListener() {
        db.collection("events")
                //.whereEqualTo("privateEvent", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        allEvents.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Event event = doc.toObject(Event.class);
                            event.setEventId(doc.getId());
                            allEvents.add(event);
                        }
                        applyAllFilters();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            currentFilterAvail = data.getStringExtra("AVAIL");
            currentFilterInterest = data.getStringExtra("INTEREST");
        } else if (requestCode == FILTER_REQUEST_CODE) {
            currentFilterAvail = null;
            currentFilterInterest = null;
            Toast.makeText(this, "Filters Reset", Toast.LENGTH_SHORT).show();
        }
        applyAllFilters();
    }

    /**
     * Single entry point for all filtering. Always runs search first,
     * then applies availability and interest filters on top of the search results.
     * This ensures 01.01.06 - search and filter always work together.
     */
    private void applyAllFilters() {
        // Step 1: apply keyword search against allEvents
        ArrayList<Event> searchResults = new ArrayList<>();
        for (Event event : allEvents) {
            if (currentSearchQuery.isEmpty() ||
                    (event.getName() != null && event.getName().toLowerCase().contains(currentSearchQuery.toLowerCase()))) {
                searchResults.add(event);
            }
        }

        // Step 2: determine if any filter is active
        String filterAvail = (currentFilterAvail == null) ? "availability" : currentFilterAvail.toLowerCase();
        String filterInterest = (currentFilterInterest == null) ? "interests" : currentFilterInterest.toLowerCase();

        boolean filterByAvail = filterAvail.equals("open") || filterAvail.equals("full");
        boolean filterByInterest = !filterInterest.equals("interests") && !filterInterest.equals("all");

        // Step 3: apply interest filter on top of search results
        ArrayList<Event> interestResults = new ArrayList<>();
        for (Event event : searchResults) {
            if (!filterByInterest) {
                interestResults.add(event);
            } else {
                String name = (event.getName() != null) ? event.getName().toLowerCase() : "";
                String category = (event.getCategory() != null) ? event.getCategory().toLowerCase() : "";
                if (category.contains(filterInterest) || name.contains(filterInterest)) {
                    interestResults.add(event);
                }
            }
        }

        // Step 4: if no availability filter, we're done
        if (!filterByAvail) {
            filteredEvents.clear();
            filteredEvents.addAll(interestResults);
            adapter.notifyDataSetChanged();
            return;
        }

        // Step 5: async availability check on top of interest results
        boolean wantOpen = filterAvail.equals("open");
        ArrayList<Event> finalResults = new ArrayList<>();
        int[] remaining = {interestResults.size()};

        if (remaining[0] == 0) {
            filteredEvents.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        for (Event event : interestResults) {
            int limit = event.getListLimit();

            if (limit <= 0 || limit == Integer.MAX_VALUE) {
                if (wantOpen) finalResults.add(event);
                remaining[0]--;
                if (remaining[0] == 0) finishFilter(finalResults);
                continue;
            }

            db.collection("events")
                    .document(event.getEventId())
                    .collection("participants")
                    .whereEqualTo("status", "waitlist")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        int count = snapshot.size();
                        boolean isFull = count >= limit;
                        if ((wantOpen && !isFull) || (!wantOpen && isFull)) {
                            finalResults.add(event);
                        }
                        remaining[0]--;
                        if (remaining[0] == 0) finishFilter(finalResults);
                    })
                    .addOnFailureListener(e -> {
                        remaining[0]--;
                        if (remaining[0] == 0) finishFilter(finalResults);
                    });
        }
    }

    private void finishFilter(ArrayList<Event> result) {
        filteredEvents.clear();
        filteredEvents.addAll(result);
        adapter.notifyDataSetChanged();
        if (filteredEvents.isEmpty()) {
            Toast.makeText(this, "No events match those filters", Toast.LENGTH_SHORT).show();
        }
    }
}
