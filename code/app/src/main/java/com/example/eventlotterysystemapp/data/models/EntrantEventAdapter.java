package com.example.eventlotterysystemapp.data.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
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

        // Cancel any existing listeners from a previously bound event before attaching new ones.
        // This is the core fix for the "joining one event joins others" bug.
        holder.cancelListeners();

        String eventId = event.getEventId();

        holder.eventName.setText(event.getName());
        holder.eventName.setOnClickListener(v -> {
            if (eventId != null) {
                android.content.Intent intent = new android.content.Intent(context, com.example.eventlotterysystemapp.ui.EntrantCommentActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                intent.putExtra("USER_EMAIL", email);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        if (event.getDateTimeAsDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, h:mm a", java.util.Locale.getDefault());
            holder.eventDate.setText("📅 " + sdf.format(event.getDateTimeAsDate()));
        } else {
            holder.eventDate.setText("📅 TBD");
        }

        // Listener 1: waitlist count and full/open status
        ListenerRegistration capacityReg = db.collection("events")
                .document(eventId)
                .collection("participants")
                .whereEqualTo("status", "waitlist")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    int currentWaitlistSize = snapshot.size();
                    int limit = event.getListLimit();

                    String limitStr;
                    if (limit == Integer.MAX_VALUE || limit <= 0) {
                        limitStr = "\u221E";
                    } else {
                        limitStr = String.valueOf(limit);
                    }

                    holder.eventCapacity.setText("👥 " + currentWaitlistSize + "/" + limitStr);

                    boolean isFull = (limit > 0 && limit != Integer.MAX_VALUE && currentWaitlistSize >= limit);
                    if (isFull && "Join".equalsIgnoreCase(holder.btnJoin.getText().toString())) {
                        holder.btnJoin.setEnabled(false);
                        holder.btnJoin.setText("Full");
                    } else if (!isFull) {
                        holder.btnJoin.setEnabled(true);
                    }
                });
        holder.capacityListenerReg = capacityReg;

        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.eventPoster);

        if (email == null || email.isEmpty()) {
            holder.btnJoin.setEnabled(false);
            return;
        }

        final String[] currentStatus = {"none"};
        DocumentReference userRef = db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(email);

        // Listener 2: this user's join status for this specific event
        ListenerRegistration statusReg = userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                android.util.Log.e("FIRESTORE_STATUS", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String status = snapshot.getString("status");
                currentStatus[0] = (status == null || status.isEmpty()) ? "none" : status;

                holder.tvJoinedBadge.setVisibility(View.VISIBLE);
                holder.tvJoinedBadge.setText(currentStatus[0].toUpperCase());

                if ("selected".equalsIgnoreCase(currentStatus[0])) {
                    holder.tvJoinedBadge.setBackgroundColor(android.graphics.Color.GREEN);
                } else if ("waitlist".equalsIgnoreCase(currentStatus[0])) {
                    holder.tvJoinedBadge.setBackgroundColor(android.graphics.Color.parseColor("#1A237E"));
                } else if ("cancelled".equalsIgnoreCase(currentStatus[0])) {
                    holder.tvJoinedBadge.setBackgroundColor(android.graphics.Color.RED);
                }

                if ("cancelled".equalsIgnoreCase(currentStatus[0]) || "none".equalsIgnoreCase(currentStatus[0])) {
                    holder.btnJoin.setText("Join");
                } else {
                    holder.btnJoin.setText("Leave");
                }
            } else {
                currentStatus[0] = "none";
                holder.btnJoin.setText("Join");
            }
        });
        holder.statusListenerReg = statusReg;

        holder.btnJoin.setOnClickListener(v -> {
            String buttonText = holder.btnJoin.getText().toString();
            String status = currentStatus[0];

            if ("Join".equalsIgnoreCase(buttonText)) {
                if (status == null || status.isEmpty() || "none".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
                    Participant participant = new Participant(email, "waitlist");
                    userRef.set(participant)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(context, "Joined waitlist!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(err -> {
                                android.util.Log.e("FIRESTORE_ERROR", err.getMessage());
                                Toast.makeText(context, "Error: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(context, "Cannot join. Current status: " + status, Toast.LENGTH_SHORT).show();
                }
            } else {
                userRef.update("status", "cancelled")
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(context, "Left event.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(err ->
                                userRef.set(new Participant(email, "cancelled")));
            }
        });

        if (AccessibilityUtils.isAccessibilityModeOn(holder.itemView.getContext())) {
            holder.eventName.setTextSize(26);
            holder.eventDate.setTextSize(16);
            holder.eventCapacity.setTextSize(16);

            holder.btnJoin.setTextSize(15);
            holder.btnJoin.setMinHeight(65);
        }
    }

    // Called by RecyclerView when a ViewHolder goes off screen - clean up listeners
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
        android.widget.ImageButton btnThreedotsMenu;

        // Store listener registrations so they can be cancelled on rebind/recycle
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
        }

        public void cancelListeners() {
            if (capacityListenerReg != null) {
                capacityListenerReg.remove();
                capacityListenerReg = null;
            }
            if (statusListenerReg != null) {
                statusListenerReg.remove();
                statusListenerReg = null;
            }
        }
    }
}