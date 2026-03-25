package com.example.eventlotterysystemapp.data.models;

import android.content.Context;
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
import java.util.List;

public class EntrantEventAdapter extends RecyclerView.Adapter<EntrantEventAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;

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
        holder.eventName.setText(event.getName());

        // Show the location or date in the details section
        holder.eventDate.setText(event.getLocation());

        // Load the image the Organizer uploaded
        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.eventPoster);

        holder.btnJoin.setOnClickListener(v -> {
            // Logic for joining the lottery goes here later
        });
    }

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