package com.example.eventlotterysystemapp.data.models;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Notification;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.eventlotterysystemapp.data.models.Participant;

public class EntrantNotificationAdapter extends RecyclerView.Adapter<EntrantNotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private Context context;
    private FirebaseFirestore db;

    public EntrantNotificationAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvMessage.setText(notification.getMessage());

        // Format timestamp if needed, otherwise just show relative time
        holder.tvTimestamp.setText(notification.getTimestamp() != null ?
                notification.getTimestamp().toDate().toString() : "");

        // Determine if this is an "Actionable" notification
        String type = notification.getType();
        boolean isActionable = "LOTTERY_WIN".equals(type) ||
                "PRIVATE_INVITE".equals(type) ||
                "COORG_INVITE".equals(type);

        // Only show buttons if it's actionable AND hasn't been responded to yet
        if (isActionable && "pending".equals(notification.getStatus())) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnDecline.setVisibility(View.VISIBLE);
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnDecline.setVisibility(View.GONE);
        }

        // Set button listeners
        holder.btnAccept.setOnClickListener(v -> handleResponse(notification, "accepted", position));
        holder.btnDecline.setOnClickListener(v -> handleResponse(notification, "declined", position));
    }

    /**
     * Handles the Entrant's choice to accept or decline (US 01.05.07 / 01.05.02)
     */
    private void handleResponse(Notification notification, String action, int position) {
        db.collection("notifications").document(notification.getId())
                .update("status", action)
                .addOnSuccessListener(aVoid -> {
                    String type = notification.getType();

                    if ("COORG_INVITE".equals(type)) {
                        if ("accepted".equals(action)) {
                            // Create the participant with the specific "co-organizer" status
                            Map<String, Object> participant = new HashMap<>();
                            participant.put("email", notification.getRecipientEmail());
                            participant.put("status", "co-organizer");

                            db.collection("events").document(notification.getEventId())
                                    .collection("participants").document(notification.getRecipientEmail())
                                    .set(participant)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "You are now a Co-Organizer!", Toast.LENGTH_SHORT).show();
                                        notification.setStatus(action);
                                        notifyItemChanged(position);
                                    });
                        } else {
                            Toast.makeText(context, "Invitation declined", Toast.LENGTH_SHORT).show();
                            notification.setStatus(action);
                            notifyItemChanged(position);
                        }
                    } else if ("PRIVATE_INVITE".equals(type)) {
                        if ("accepted".equals(action)) {
                            Participant participant = new Participant(notification.getRecipientEmail(), "waitlist");

                            db.collection("events").document(notification.getEventId())
                                    .collection("participants").document(notification.getRecipientEmail())
                                    .set(participant)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Invitation accepted", Toast.LENGTH_SHORT).show();
                                        notification.setStatus(action);
                                        notifyItemChanged(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(context, "Invitation declined", Toast.LENGTH_SHORT).show();
                            notification.setStatus(action);
                            notifyItemChanged(position);
                        }
                    } else {
                        String participantStatus = action.equals("accepted") ? "enrolled" : "cancelled";

                        db.collection("events").document(notification.getEventId())
                                .collection("participants").document(notification.getRecipientEmail())
                                .update("status", participantStatus)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Invitation " + action, Toast.LENGTH_SHORT).show();
                                    notification.setStatus(action);
                                    notifyItemChanged(position);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error updating notification", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        Button btnAccept, btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTimestamp = itemView.findViewById(R.id.tvNotificationTimestamp);
            btnAccept = itemView.findViewById(R.id.btnAcceptInvite);
            btnDecline = itemView.findViewById(R.id.btnDeclineInvite);
        }
    }
}