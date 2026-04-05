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

    public EntrantEventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
        this.db = FirebaseFirestore.getInstance();

        if (getUser() != null) {
            this.email = getUser().getEmail();
        } else {
            this.email = "";
        }
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

        if (email == null || email.isEmpty()) {
            if (getUser() != null) email = getUser().getEmail();
        }

        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getLocation());

        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.eventPoster);

        if (email == null || email.isEmpty()) {
            holder.btnJoin.setEnabled(false);
            return;
        } else {
            holder.btnJoin.setEnabled(true);
        }

        // Tracking current status for logic checks
        final String[] currentStatus = {"none"};

        DocumentReference userRef = db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(email);

        userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) return;

            if (snapshot != null && snapshot.exists()) {
                String status = snapshot.getString("status");
                currentStatus[0] = (status != null) ? status : "none";

                if ("cancelled".equalsIgnoreCase(status)) {
                    holder.btnJoin.setText("Join");
                } else {
                    holder.btnJoin.setText("Leave");
                }
            } else {
                currentStatus[0] = "none";
                holder.btnJoin.setText("Join");
            }
        });

        holder.btnJoin.setOnClickListener(v -> {
            String buttonText = holder.btnJoin.getText().toString();
            String status = currentStatus[0];

            if ("Join".equalsIgnoreCase(buttonText)) {
                // Allowed to join if status is none, empty, or cancelled
                if (status == null || status.isEmpty() || "none".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {

                    Participant participant = new Participant(email, "waitlist");

                    // Use .set() to either create or overwrite the existing "cancelled" entry
                    userRef.set(participant)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Joined waitlist!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(err -> {
                                Toast.makeText(context, "Error joining: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(context, "You are already active (Status: " + status + ")", Toast.LENGTH_SHORT).show();
                }

            } else {
                // "Leave" Logic
                userRef.update("status", "cancelled")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Status updated to cancelled.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(err -> {
                            // If update fails (doc doesn't exist), create a cancelled entry
                            Participant cancelledParticipant = new Participant(email, "cancelled");
                            userRef.set(cancelledParticipant)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Left event.", Toast.LENGTH_SHORT));
                        });
            }
        });
    }

    @Override
    public int getItemCount() { return events != null ? events.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate;
        ImageView eventPoster;
        Button btnJoin;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventPoster = itemView.findViewById(R.id.eventPoster);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }
    }
}