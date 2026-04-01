package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Participant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class InviteEntrantActivity extends AppCompatActivity {

    private EditText searchEntrant;
    private Button btnSearchEntrant;
    private ListView listEntrants;

    private Button btnBack;
    private FirebaseFirestore db;
    private ArrayList<String> entrantResults;
    private ArrayAdapter<String> adapter;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_entrant);

        eventId = getIntent().getStringExtra("EVENT_ID");

        searchEntrant = findViewById(R.id.searchEntrant);
        btnSearchEntrant = findViewById(R.id.btnSearchEntrant);
        listEntrants = findViewById(R.id.listEntrants);
        btnBack = findViewById(R.id.backBtn);

        db = FirebaseFirestore.getInstance();
        entrantResults = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                entrantResults
        );
        listEntrants.setAdapter(adapter);

        btnSearchEntrant.setOnClickListener(v -> searchUsers());

        listEntrants.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEmail = entrantResults.get(position);

            // If the display text is "Name - Email", extract just the email
            if (selectedEmail.contains(" - ")) {
                selectedEmail = selectedEmail.substring(selectedEmail.lastIndexOf(" - ") + 3);
            }

            inviteUserToWaitlist(selectedEmail);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void searchUsers() {
        String query = searchEntrant.getText().toString().trim().toLowerCase();

        db.collection("users")
                .whereEqualTo("role", "Entrant")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    entrantResults.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");

                        String safeName = name != null ? name : "";
                        String safeEmail = email != null ? email : "";

                        if (safeName.toLowerCase().contains(query) || safeEmail.toLowerCase().contains(query)) {
                            entrantResults.add(safeName + " - " + safeEmail);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (entrantResults.isEmpty()) {
                        Toast.makeText(this, "No matching entrants found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Search failed", Toast.LENGTH_SHORT).show());
    }

    private void inviteUserToWaitlist(String email) {
        db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(email)
                .set(new Participant(email, "waitlist"))
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Entrant invited to waitlist", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to invite entrant", Toast.LENGTH_SHORT).show());
    }
}
