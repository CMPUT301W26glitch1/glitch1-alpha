package com.example.eventlotterysystemapp.data.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Participant adapter used to display participants as a list in ParticipantListFragment
 */
public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {
    private List<Participant> participants;

    public ParticipantAdapter(List<Participant> participants) {
        this.participants = participants;
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

        // Fetch real user name from the main "users" collection
        // Note: Using document ID directly because UserController uses email as the ID
        FirebaseFirestore.getInstance().collection("users")
                .document(p.getEmail())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        // Ensure we are still binding to the correct position after the async call
                        if (holder.getBindingAdapterPosition() == position) {
                            holder.name.setText(name != null ? name : "Unknown User");
                        }
                    }
                })
                .addOnFailureListener(e -> holder.name.setText("Error loading name"));
    }

    @Override
    public int getItemCount() {
        return participants != null ? participants.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.participantName);
            email = itemView.findViewById(R.id.participantEmail);
        }
    }
}