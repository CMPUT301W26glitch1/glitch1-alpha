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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email = getUser().getEmail();



    public EntrantEventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This uses your specific Entrant XML
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Event event = events.get(position);
        String eventId = event.getEventId();

        holder.eventName.setText(event.getName());

        // Show the location or date in the details section
        holder.eventDate.setText(event.getLocation());

        // Load the image the Organizer uploaded
        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.eventPoster);


        //reference to the user's document
        DocumentReference userRef = db.collection("events")
                .document(eventId)
                .collection("participants")
                .document(getUser().getEmail());

        //Make the button leave or join
        userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Handle error
                return;
            }

            if (snapshot != null && snapshot.exists()) {

                //Gets the status of the user (waitlist, etc)
                String status = snapshot.getString("status");

                //If on waitlist then button now says "Leave"
                if ("waitlist".equals(status)) {
                    holder.btnJoin.setText("Leave");
                } else {
                    holder.btnJoin.setText("Join");
                }
            } else {
                // User not in list at all
                holder.btnJoin.setText("Join");
            }
        });

        holder.btnJoin.setOnClickListener(v -> {




            if (holder.btnJoin.getText().toString() == "Join") {
                Participant participant = new Participant(email, "waitlist");

                db.collection("events")
                        .document(eventId)
                        .collection("participants")
                        .document(email) // use email as unique ID
                        .set(participant)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Joined waitlist!", Toast.LENGTH_SHORT).show();
                            //holder.btnJoin.setEnabled(false);
                            holder.btnJoin.setText("Leave");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }  else{
                db.collection("events")
                        .document(eventId)
                        .collection("participants")
                        .document(email) // Use same unique ID (email)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Left waitlist", Toast.LENGTH_SHORT).show();
                            holder.btnJoin.setText("Join");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }
        });    }

    @Override
    public int getItemCount() { return events.size(); }

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