package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class AdminManageOrganizersActivity extends AppCompatActivity {

    private RecyclerView recyclerOrganizers;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> organizerList = new ArrayList<>();
    private OrganizerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_organizers);
        AccessibilityUtils.applyAccessibilityMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Organizers");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        recyclerOrganizers = findViewById(R.id.recyclerOrganizers);
        recyclerOrganizers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrganizerAdapter();
        recyclerOrganizers.setAdapter(adapter);

        loadOrganizers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrganizers();
    }

    private void loadOrganizers() {
        db.collection("users")
                .whereEqualTo("role", "Organizer")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    organizerList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        organizerList.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading organizers", Toast.LENGTH_SHORT).show());
    }

    private void deleteOrganizer(String documentId) {
        db.collection("users")
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Organizer removed", Toast.LENGTH_SHORT).show();
                    loadOrganizers();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error removing organizer", Toast.LENGTH_SHORT).show());
    }

    private class OrganizerAdapter extends RecyclerView.Adapter<OrganizerAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_organizer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            QueryDocumentSnapshot doc = organizerList.get(position);

            String name = doc.getString("name");
            String email = doc.getString("email");

            holder.tvName.setText(name != null ? name : "No Name");
            holder.tvEmail.setText(email != null ? email : "No Email");

            holder.btnDelete.setOnClickListener(v ->
                    deleteOrganizer(doc.getId()));
            holder.itemView.post(() -> AccessibilityUtils.applyToItemView(holder.itemView.getContext(), holder.itemView));
        }

        @Override
        public int getItemCount() {
            return organizerList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail;
            ImageButton btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                btnDelete = itemView.findViewById(R.id.btnDeleteOrganizer);
            }
        }
    }
}