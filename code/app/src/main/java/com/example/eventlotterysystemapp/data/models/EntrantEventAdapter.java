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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        // Using the email from your login (test3@gmail.com)
        String userEmail = "test3@gmail.com";

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

        // 4. JOIN/LEAVE ACTION (Matches aarib444's screenshot logic)
        holder.btnJoin.setOnClickListener(v -> {
            boolean isCurrentlyJoined = holder.btnJoin.getText().toString().equalsIgnoreCase("Leave");

            com.google.firebase.firestore.DocumentReference pRef = db.collection("events")
                    .document(event.getEventId())
                    .collection("participants")
                    .document(userEmail);

            if (!isCurrentlyJoined) {
                // JOINING: Create the map exactly like the teammate's test method
                java.util.Map<String, Object> testParticipant = new java.util.HashMap<>();
                testParticipant.put("email", userEmail);
                testParticipant.put("status", "waitlist");

                pRef.set(testParticipant).addOnSuccessListener(aVoid -> {
                    android.widget.Toast.makeText(context, "Added to Waitlist!", android.widget.Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position); // Refresh UI
                });
            } else {
                // LEAVING: Delete the document from the sub-collection
                pRef.delete().addOnSuccessListener(aVoid -> {
                    android.widget.Toast.makeText(context, "Removed from Event", android.widget.Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position); // Refresh UI
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, badge;
        Button btnJoin;
        ImageButton ivEventOptions;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            badge = itemView.findViewById(R.id.tvJoinedBadge);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            ivEventOptions = itemView.findViewById(R.id.btnThreeDotsMenu);
        }
    }
}