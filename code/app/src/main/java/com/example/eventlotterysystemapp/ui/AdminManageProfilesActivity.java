package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

// AdminManageProfilesActivity fetches all user profiles from Firestore
// and displays them in a scrollable list. Tapping a profile opens its detail screen.
public class AdminManageProfilesActivity extends AppCompatActivity {

    private RecyclerView recyclerProfiles;
    private FirebaseFirestore db;

    // in-memory list of user documents fetched from Firestore
    private List<QueryDocumentSnapshot> profileList = new ArrayList<>();
    private ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_profiles);
        AccessibilityUtils.applyAccessibilityMode(this);

        // set up toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Profiles");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        recyclerProfiles = findViewById(R.id.recyclerProfiles);
        recyclerProfiles.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProfileAdapter();
        recyclerProfiles.setAdapter(adapter);

        loadProfiles();
    }

    // reload the list every time we return to this screen (e.g. after a deletion)
    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    // fetch all documents from the users collection
    private void loadProfiles() {
        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    profileList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        profileList.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading profiles: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // adapter that binds each Firestore user document to a row in the RecyclerView
    private class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

        // creates a new view for each row in the list using the item_profile.xml layout
        @Override
        public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_profile, parent, false);
            return new ProfileViewHolder(view);
        }

        // binds user data to the TextViews in each row
        @Override
        public void onBindViewHolder(ProfileViewHolder holder, int position) {
            QueryDocumentSnapshot doc = profileList.get(position);

            String name  = doc.getString("name");
            String email = doc.getString("email");
            String role  = doc.getString("role");

            // if the field is null show a default string instead
            holder.tvName.setText(name != null ? name : "No name");
            holder.tvEmail.setText(email != null ? email : "No email");
            holder.tvRole.setText(role != null ? role : "No role");

            // tapping a profile card passes its data to AdminProfileDetailActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminManageProfilesActivity.this, AdminProfileDetailActivity.class);
                intent.putExtra("profileId", doc.getId());
                intent.putExtra("profileName", name);
                intent.putExtra("profileEmail", email);
                intent.putExtra("profileRole", role);
                Long phone = doc.getLong("phoneNumber");
                intent.putExtra("profilePhone",
                        (phone != null && phone != -1)
                                ? String.valueOf(phone)
                                : null);
                startActivity(intent);
            });
            holder.itemView.post(() -> AccessibilityUtils.applyToItemView(holder.itemView.getContext(), holder.itemView));
        }

        @Override
        public int getItemCount() {
            return profileList.size();
        }

        // holds references to the TextViews in each item_profile.xml row
        class ProfileViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvRole;

            ProfileViewHolder(View itemView) {
                super(itemView);
                tvName  = itemView.findViewById(R.id.tvProfileName);
                tvEmail = itemView.findViewById(R.id.tvProfileEmail);
                tvRole  = itemView.findViewById(R.id.tvProfileRole);
            }
        }
    }
}