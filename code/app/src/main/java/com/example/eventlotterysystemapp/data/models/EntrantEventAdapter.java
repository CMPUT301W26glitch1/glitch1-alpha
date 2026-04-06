package com.example.eventlotterysystemapp.data.models;

import static com.example.eventlotterysystemapp.data.models.UserSession.getUser;

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

        String eventId = event.getEventId();

        holder.eventName.setText(event.getName());

        if (event.getDateTimeAsDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, h:mm a", java.util.Locale.getDefault());
            holder.eventDate.setText("📅 " + sdf.format(event.getDateTimeAsDate()));
        } else {
            holder.eventDate.setText("📅 TBD");
        }

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .whereEqualTo("status", "waitlist")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    int currentWaitlistSize = snapshot.size();
                    int limit = event.getListLimit();

                    // Logic for Unlimited size (Integer.MAX_VALUE or 0)
                    String limitStr;
                    if (limit == Integer.MAX_VALUE || limit <= 0) {
                        limitStr = "\u221E"; // Infinity symbol (∞)
                    } else {
                        limitStr = String.valueOf(limit);
                    }

                    holder.eventCapacity.setText("👥 " + currentWaitlistSize + "/" + limitStr);

                    // Button logic for full waitlist
                    boolean isFull = (limit > 0 && limit != Integer.MAX_VALUE && currentWaitlistSize >= limit);

                    if (isFull && "Join".equalsIgnoreCase(holder.btnJoin.getText().toString())) {
                        holder.btnJoin.setEnabled(false);
                        holder.btnJoin.setText("Full");
                    } else {
                        holder.btnJoin.setEnabled(true);
                    }
                });

        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.eventPoster);

        // --- Status & Button Logic ---
        if (email == null || email.isEmpty()) {
            holder.btnJoin.setEnabled(false);
            return;
        }

        final String[] currentStatus = {"none"};
        DocumentReference userRef = db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(email);

        userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                android.util.Log.e("FIRESTORE_STATUS", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String status = snapshot.getString("status");
                currentStatus[0] = (status == null || status.isEmpty()) ? "none" : status;

                android.util.Log.d("FIRESTORE_STATUS", "Event: " + event.getName() + " | Status Found: " + currentStatus[0]);

                if ("cancelled".equalsIgnoreCase(currentStatus[0]) || "none".equalsIgnoreCase(currentStatus[0])) {
                    holder.btnJoin.setText("Join");
                } else {
                    holder.btnJoin.setText("Leave");
                }
            } else {
                currentStatus[0] = "none";
                android.util.Log.d("FIRESTORE_STATUS", "No document for " + event.getName() + ". Setting status to none.");
                holder.btnJoin.setText("Join");
            }
        });

        holder.btnJoin.setOnClickListener(v -> {
            String buttonText = holder.btnJoin.getText().toString();
            String status = currentStatus[0];

            // Debug
            android.util.Log.d("BUTTON_CLICK", "Button: " + buttonText + " | Status: " + status);

            if ("Join".equalsIgnoreCase(buttonText)) {
                if (status == null || status.isEmpty() || "none".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {

                    Participant participant = new Participant(email, "waitlist");

                    userRef.set(participant)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Joined waitlist!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(err -> {
                                android.util.Log.e("FIRESTORE_ERROR", err.getMessage());
                                Toast.makeText(context, "Error: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // This handles cases where status might be "selected" or "enrolled"
                    Toast.makeText(context, "Cannot join. Current status: " + status, Toast.LENGTH_SHORT).show();
                }

            } else {
                // LEAVE LOGIC
                userRef.update("status", "cancelled")
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Left event.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(err -> {
                            userRef.set(new Participant(email, "cancelled"));
                        });
            }
        });
    }

    @Override
    public int getItemCount() { return events != null ? events.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventCapacity; // Add eventCapacity here
        ImageView eventPoster;
        Button btnJoin;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventCapacity = itemView.findViewById(R.id.eventCapacity); // Link it here
            eventPoster = itemView.findViewById(R.id.eventPoster);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }
    }
}