package com.example.eventlotterysystemapp.data.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton; // Added
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.EntrantCommentActivity; // Added
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

import java.util.List;

public class EntrantEventAdapter extends RecyclerView.Adapter<EntrantEventAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;
    private FirebaseFirestore db;
    private String email;

    public EntrantEventAdapter(Context context, List<Event> events, String email) {
        this.context = context;
        this.events = events;
        this.db = FirebaseFirestore.getInstance();
        this.email = (email != null) ? email.toLowerCase().trim() : "";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        if (event == null) return;

        holder.cancelListeners();
        String eventId = event.getEventId();

        holder.eventName.setText(event.getName());

        View.OnClickListener openComments = v -> {
            if (eventId != null) {
                Intent intent = new Intent(context, EntrantCommentActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                intent.putExtra("USER_EMAIL", email);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        };

        holder.eventName.setOnClickListener(openComments);
        holder.btnComments.setOnClickListener(openComments);
        holder.btnComments.setVisibility(View.VISIBLE);

        if (event.getDateTimeAsDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, h:mm a", java.util.Locale.getDefault());
            holder.eventDate.setText("📅 " + sdf.format(event.getDateTimeAsDate()));
        } else {
            holder.eventDate.setText("📅 TBD");
        }

        holder.capacityListenerReg = db.collection("events")
                .document(eventId)
                .collection("participants")
                .whereEqualTo("status", "waitlist")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    int currentWaitlistSize = snapshot.size();
                    int limit = event.getListLimit();

                    String limitStr = (limit == Integer.MAX_VALUE || limit <= 0) ? "\u221E" : String.valueOf(limit);
                    holder.eventCapacity.setText("👥 " + currentWaitlistSize + "/" + limitStr);

                    boolean isFull = (limit > 0 && limit != Integer.MAX_VALUE && currentWaitlistSize >= limit);
                    if (isFull && "Join".equalsIgnoreCase(holder.btnJoin.getText().toString())) {
                        holder.btnJoin.setEnabled(false);
                        holder.btnJoin.setText("Full");
                    } else if (!isFull) {
                        holder.btnJoin.setEnabled(true);
                    }
                });

        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.eventPoster);

        if (email.isEmpty()) {
            holder.btnJoin.setEnabled(false);
            return;
        }

        final String[] currentStatus = {"none"};
        DocumentReference userRef = db.collection("events").document(eventId).collection("participants").document(email);

        holder.statusListenerReg = userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null || snapshot == null) return;

            if (snapshot.exists()) {
                String status = snapshot.getString("status");
                currentStatus[0] = (status == null || status.isEmpty()) ? "none" : status;

                holder.tvJoinedBadge.setVisibility(View.VISIBLE);
                holder.tvJoinedBadge.setText(currentStatus[0].toUpperCase());

                // Set colors based on status
                int color = android.graphics.Color.parseColor("#1A237E"); // Default blue
                if ("selected".equalsIgnoreCase(currentStatus[0])) color = android.graphics.Color.GREEN;
                else if ("cancelled".equalsIgnoreCase(currentStatus[0])) color = android.graphics.Color.RED;

                holder.tvJoinedBadge.setBackgroundColor(color);
                holder.btnJoin.setText(("cancelled".equalsIgnoreCase(currentStatus[0]) || "none".equalsIgnoreCase(currentStatus[0])) ? "Join" : "Leave");
            } else {
                currentStatus[0] = "none";
                holder.btnJoin.setText("Join");
                holder.tvJoinedBadge.setVisibility(View.GONE);
            }
        });

        holder.btnJoin.setOnClickListener(v -> {
            if ("Join".equalsIgnoreCase(holder.btnJoin.getText().toString())) {
                Participant p = new Participant(email, "waitlist");
                userRef.set(p).addOnSuccessListener(aVoid -> Toast.makeText(context, "Joined waitlist!", Toast.LENGTH_SHORT).show());
            } else {
                userRef.update("status", "cancelled").addOnSuccessListener(aVoid -> Toast.makeText(context, "Left event.", Toast.LENGTH_SHORT).show());
            }
        });

        if (AccessibilityUtils.isAccessibilityModeOn(holder.itemView.getContext())) {
            holder.eventName.setTextSize(26);
            holder.btnJoin.setTextSize(15);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelListeners();
    }

    @Override
    public int getItemCount() { return events != null ? events.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventCapacity, tvJoinedBadge;
        ImageView eventPoster;
        Button btnJoin;
        ImageButton btnThreedotsMenu, btnComments; // Added btnComments

        ListenerRegistration capacityListenerReg;
        ListenerRegistration statusListenerReg;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventCapacity = itemView.findViewById(R.id.eventCapacity);
            eventPoster = itemView.findViewById(R.id.eventPoster);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            tvJoinedBadge = itemView.findViewById(R.id.tvJoinedBadge);
            btnThreedotsMenu = itemView.findViewById(R.id.btnThreeDotsMenu);
            // NEW: Initialize the comments button
            btnComments = itemView.findViewById(R.id.btnComments);
        }

        public void cancelListeners() {
            if (capacityListenerReg != null) capacityListenerReg.remove();
            if (statusListenerReg != null) statusListenerReg.remove();
            capacityListenerReg = null;
            statusListenerReg = null;
        }
    }
}