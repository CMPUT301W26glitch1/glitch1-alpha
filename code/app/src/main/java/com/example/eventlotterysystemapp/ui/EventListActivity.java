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
import com.example.eventlotterysystemapp.data.models.NotificationManager;
import com.example.eventlotterysystemapp.data.models.EntrantEventAdapter; // Ensure this import matches your project
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    private static final int FILTER_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");
        // 1. Initialize UI
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        searchView = findViewById(R.id.searchEvents);
        btnFilter = findViewById(R.id.btnFilter);
        fabNotifications = findViewById(R.id.fabNotifications);
        fabCamera = findViewById(R.id.fabCamera);
        tvUserIcon = findViewById(R.id.tvUserIcon);
        ivMainOptions = findViewById(R.id.ivMainOptions);

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabNotifications.setOnClickListener(v -> {
            // sendTestNotification();
            Intent intent = new Intent(EventListActivity.this, EntrantNotificationListActivity.class);
            intent.putExtra("USER_EMAIL", loggedInUserEmail);
            startActivity(intent);
        });

        // 2. Data setup
        allEvents = new ArrayList<>();
        filteredEvents = new ArrayList<>();

        // Pass 'this' as context for Glide/Layout inflation
        adapter = new EntrantEventAdapter(this, filteredEvents, loggedInUserEmail);
        eventRecyclerView.setAdapter(adapter);

        // 3. Initialize Firebase and Start Listening
        db = FirebaseFirestore.getInstance();
        startFirebaseListener();

        // 4. Click Listeners
        tvUserIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        ivMainOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Refresh List"); // This will now trigger the listener update
            popup.getMenu().add("Lottery Info");
            popup.getMenu().add("Settings");
            popup.getMenu().add("Logout");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Refresh List")) {
                    startFirebaseListener(); // Re-triggering refresh
                    return true;
                } else if (item.getTitle().equals("Lottery Info")) {
                    startActivity(new Intent(EventListActivity.this, LotteryInfoActivity.class));
                    return true;
                } else if (item.getTitle().equals("Logout")) {
                    // 1. Create the Intent to go back to the Sign In page
                    // (Change 'SignInActivity.class' to the actual name of your login file!)
                    Intent intent = new Intent(EventListActivity.this, LoginActivity.class);

                    // 2. Clear the history so they can't "Back" into the app after logging out
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                    // 3. Optional: Show a toast
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
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

        // 5. Search Logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBySearch(newText);
                return true;
            }
        });
    }

    private void startFirebaseListener() {
        db.collection("events")
                .whereEqualTo("privateEvent", false)
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
                filteredEvents.clear();
                filteredEvents.addAll(allEvents);
                adapter.notifyDataSetChanged();
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String avail = data.getStringExtra("AVAIL");
            String interest = data.getStringExtra("INTEREST");
            applyAdvancedFilters(avail, interest);
        }
        else{
            resetFilters();
        }


        }
private void resetFilters() {
    filteredEvents.clear();
    filteredEvents.addAll(allEvents);
    adapter.notifyDataSetChanged();
    Toast.makeText(this, "Filters Reset", Toast.LENGTH_SHORT).show();
}

    private void applyAdvancedFilters(String avail, String interest) {
        filteredEvents.clear();

        // 1. Handle nulls and convert to lowercase
        String filterAvail = (avail == null) ? "all" : avail.toLowerCase();
        String filterInterest = (interest == null) ? "all" : interest.toLowerCase();

        for (Event event : allEvents) {
            // Use the actual data from your Event object
            String name = (event.getName() != null) ? event.getName().toLowerCase() : "";
            String category = (event.getCategory() != null) ? event.getCategory().toLowerCase() : "";

            // 2. The Logic:
            // Match if the filter is "All" OR if the category/name contains the filter word
            boolean matchAvail = filterAvail.equals("all") || filterAvail.equals("availability")
                    || name.contains(filterAvail);

            boolean matchInterest = filterInterest.equals("all") || filterInterest.equals("interests")
                    || category.contains(filterInterest)
                    || name.contains(filterInterest);

            if (matchAvail && matchInterest) {
                filteredEvents.add(event);
            }
        }

        // 3. Tell the adapter to refresh the screen with the new filtered list
        adapter.notifyDataSetChanged();

        if (filteredEvents.isEmpty()) {
            Toast.makeText(this, "No events match those filters", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Temporary test method to verify notification delivery and UI display.
     * This simulates an organizer sending a lottery win notification.
     */
    private void sendTestNotification() {
        NotificationManager authNotifManager = new NotificationManager();

        String testRecipientId = "aqNQJhYDIfcfmj7PJKsY";
        String testEmail = "ent1@gmail.com";
        String testEventId = "3KwaMPf8RTLFs4cspVib";
        String message = "Congratulations! You have been selected for the 'Annual Tech Gala'. Please accept or decline within 24 hours.";

        authNotifManager.sendNotification(
                testRecipientId,
                testEmail,
                message,
                "LOTTERY_WIN",
                testEventId
        );

        Toast.makeText(this, "Test Notification Sent!", Toast.LENGTH_SHORT).show();
    }
}