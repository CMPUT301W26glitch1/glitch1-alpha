package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageOrganizersActivity extends AppCompatActivity {

    private RecyclerView recyclerOrganizers;
    private OrganizerAdapter adapter;
    private final List<OrganizerAdapter.OrganizerItem> organizerList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_organizers);

        recyclerOrganizers = findViewById(R.id.recyclerOrganizers);
        recyclerOrganizers.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        adapter = new OrganizerAdapter(organizerList, organizer -> removeOrganizer(organizer));
        recyclerOrganizers.setAdapter(adapter);

        loadOrganizers();
    }

    private void loadOrganizers() {
        db.collection("users")
                .whereEqualTo("role", "Organizer")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    organizerList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String email = doc.getString("email");

                        organizerList.add(new OrganizerAdapter.OrganizerItem(
                                id,
                                name != null ? name : "No name",
                                email != null ? email : "No email"
                        ));
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load organizers", Toast.LENGTH_SHORT).show());
    }

    private void removeOrganizer(OrganizerAdapter.OrganizerItem organizer) {
        db.collection("users")
                .document(organizer.id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Organizer removed", Toast.LENGTH_SHORT).show();
                    loadOrganizers();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to remove organizer", Toast.LENGTH_SHORT).show());
    }
}