package com.example.eventlotterysystemapp.data.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.ui.organizer.EventParticipantsActivity;
import com.example.eventlotterysystemapp.ui.organizer.InviteEntrantActivity;
import com.example.eventlotterysystemapp.ui.organizer.OrganizerEventCommentsActivity;
import com.example.eventlotterysystemapp.ui.organizer.QRCodeActivity;
import com.example.eventlotterysystemapp.ui.organizer.UpdatePosterActivity;

import java.util.List;

/**
 * Event adapter used for displaying event information as a list in OrganizerMainActivity
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDesc.setText(event.getDescription());

        Glide.with(context).load(event.getPosterUrl()).into(holder.poster);

        // QR Code Button
        holder.btnQR.setOnClickListener(v -> {
            Intent intent = new Intent(context, QRCodeActivity.class);
            intent.putExtra("EVENT_ID", event.getEventId());
            intent.putExtra("IS_PRIVATE", event.isPrivate());
            context.startActivity(intent);
        });

        //Update Poster Button
        holder.poster.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdatePosterActivity.class);
            intent.putExtra("EVENT_ID", event.getEventId());
            context.startActivity(intent);
        });

        // Add listener for Participant button similarly...
        holder.btnParticipants.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventParticipantsActivity.class);
            intent.putExtra("EVENT_ID", event.getEventId());
            context.startActivity(intent);
        });

        holder.btnComments.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrganizerEventCommentsActivity.class);
            intent.putExtra("eventId", event.getEventId());
            context.startActivity(intent);
        });

        // Invite Button - only for private events
        if (event.isPrivate()) {
            holder.btnInvite.setVisibility(View.VISIBLE);
            //holder.btnQR.setVisibility(View.GONE);
            holder.btnInvite.setOnClickListener(v -> {
                Intent intent = new Intent(context, InviteEntrantActivity.class);
                intent.putExtra("EVENT_ID", event.getEventId());
                context.startActivity(intent);
            });
        } else {
            holder.btnInvite.setVisibility(View.GONE);
            holder.btnQR.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() { return events.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDesc;
        ImageView poster;
        Button btnQR, btnParticipants, btnInvite, btnComments;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDesc = itemView.findViewById(R.id.eventDesc);
            poster = itemView.findViewById(R.id.eventPoster);
            btnQR = itemView.findViewById(R.id.btnQR);
            btnParticipants = itemView.findViewById(R.id.btnParticipants);
            btnInvite = itemView.findViewById(R.id.btnInvite);
            btnComments = itemView.findViewById(R.id.btnComments);
        }
    }
}