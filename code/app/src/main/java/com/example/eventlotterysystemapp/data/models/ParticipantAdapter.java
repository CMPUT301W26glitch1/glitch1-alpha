package com.example.eventlotterysystemapp.data.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Participant adapter used to display participants as a list in ParticipantListFragment.
 * Now handles context-aware button text for Selected and Enrolled tabs.
 */
public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {
    private List<Participant> participants;
    private boolean cancelBtn;
    private String eventId;

    public ParticipantAdapter(List<Participant> participants, boolean cancelBtn, String eventId) {
        this.participants = participants;
        this.cancelBtn = cancelBtn;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant p = participants.get(position);

        // Default text while loading
        holder.name.setText("Loading...");
        holder.email.setText(p.getEmail());

        // --- CONTEXT-AWARE BUTTON LOGIC ---
        if (cancelBtn) {
            holder.btnCancel.setVisibility(View.VISIBLE);

            // Update button text based on the specific participant status
            if ("enrolled".equals(p.getStatus())) {
                holder.btnCancel.setText("Remove Enrolled");
            } else if ("selected".equals(p.getStatus())) {
                holder.btnCancel.setText("Cancel Invite");
            } else {
                holder.btnCancel.setText("Cancel");
            }

            holder.btnCancel.setOnClickListener(v -> {
                FirebaseFirestore.getInstance().collection("events")
                        .document(eventId)
                        .collection("participants")
                        .document(p.getEmail())
                        .update("status", "cancelled")
                        .addOnSuccessListener(aVoid -> {
                            String msg = "enrolled".equals(p.getStatus()) ? "Participant removed" : "Invite cancelled";
                            Toast.makeText(holder.itemView.getContext(), msg, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Operation failed", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }

        // Fetch user name from 'users' collection
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", p.getEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (holder.getBindingAdapterPosition() != position) return;

                    if (!querySnapshot.isEmpty()) {
                        String name = querySnapshot.getDocuments().get(0).getString("name");
                        holder.name.setText(name != null ? name : "Unknown User");
                    } else {
                        holder.name.setText("User Not Found");
                    }
                })
                .addOnFailureListener(e -> {
                    if (holder.getBindingAdapterPosition() == position) {
                        holder.name.setText("Error loading name");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return participants != null ? participants.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        Button btnCancel;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.participantName);
            email = itemView.findViewById(R.id.participantEmail);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}