package com.example.eventlotterysystemapp.data.models;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.EntrantEventDetailsActivity;

import java.util.List;

public class EntrantEventAdapter extends RecyclerView.Adapter<EntrantEventAdapter.ViewHolder> {
    private List<Event> events;
    private Context context;

    public EntrantEventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        String deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        // Using the email from your login (test3@gmail.com)
        String userEmail = "test3@gmail.com";
        holder.name.setText(event.getName());
        if (event.getDateTime() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd");
            String dateStr = event.getDateTime().format(formatter);

            // If there's a location, let's show it next to the date
            if (event.getLocation() != null && !event.getLocation().isEmpty()) {
                holder.tvDate.setText("📅 " + dateStr + " • " + event.getLocation());
            } else {
                holder.tvDate.setText("📅 " + dateStr);
            }
        }

        // 3. Set Capacity (Showing current participants vs limit)

        db.collection("events").document(event.getEventId())
                .collection("participants")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int actualCount = querySnapshot.size();
                    holder.tvCapacity.setText("👥 " + actualCount + "/" + event.getListLimit());
                });
        // ... Keep your Join/Leave and 3-Dots logic here ...

        // 1. Set the Event Name
        if (event != null && event.getName() != null) {
            holder.name.setText(event.getName());
        } else {
            holder.name.setText("Unnamed Event");
        }

        // 2. THE PERSISTENCE CHECK (Check Firestore so it doesn't "reset")
        db.collection("events").document(event.getEventId())
                .collection("participants").document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.btnJoin.setText("Leave");
                        holder.badge.setVisibility(View.VISIBLE);
                    } else {
                        holder.btnJoin.setText("Join");
                        holder.badge.setVisibility(View.GONE);
                    }
                });

        // 3. THREE-DOTS MENU (Passing the ID is critical for the Details Page!)
        if (holder.ivEventOptions != null) {
            holder.ivEventOptions.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
                popup.getMenu().add("Event Details");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Event Details")) {
                        android.content.Intent intent = new android.content.Intent(v.getContext(), EntrantEventDetailsActivity.class);
                        intent.putExtra("EVENT_NAME", event.getName());
                        intent.putExtra("EVENT_ID", event.getEventId()); // Matches the check in DetailsActivity
                        v.getContext().startActivity(intent);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        // REPLACE YOUR JOIN/LEAVE ACTION (SECTION 4) WITH THIS:

// US 01.07.01: Identify by Device ID instead of hardcoded email

        holder.btnJoin.setOnClickListener(v -> {
            boolean isCurrentlyJoined = holder.btnJoin.getText().toString().equalsIgnoreCase("Leave");

            // Use deviceId as the document name to stay "301 Compliant"
            com.google.firebase.firestore.DocumentReference pRef = db.collection("events")
                    .document(event.getEventId())
                    .collection("participants")
                    .document(deviceId);

            if (!isCurrentlyJoined) {
                // JOINING: Matches US 01.01.01
                java.util.Map<String, Object> participantData = new java.util.HashMap<>();
                participantData.put("deviceId", deviceId);
                participantData.put("email", userEmail); // Keep email for contact info (US 01.02.01)
                participantData.put("status", "waiting"); // Matches User Story terminology
                participantData.put("joinedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

                pRef.set(participantData).addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Added to Waiting List", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });

            } else {
                // LEAVING: Matches US 01.01.02
                pRef.delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Left Waiting List", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, tvDate, tvCapacity, badge;
        Button btnJoin;
        ImageButton ivEventOptions;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            tvDate = itemView.findViewById(R.id.eventDate);       // Matches @id/eventDate
            tvCapacity = itemView.findViewById(R.id.eventCapacity); // Matches @id/eventCapacity
            badge = itemView.findViewById(R.id.tvJoinedBadge);     // Matches @id/tvJoinedBadge
            btnJoin = itemView.findViewById(R.id.btnJoin);         // Matches @id/btnJoin
            ivEventOptions = itemView.findViewById(R.id.btnThreeDotsMenu); // Matches @id/btnThreeDotsMenu
        }
    }

   }