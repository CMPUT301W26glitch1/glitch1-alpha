package com.example.eventlotterysystemapp.data.models;

import android.content.Intent; // Added for navigation
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton; // Added for the dots
import android.widget.PopupMenu; // Added for the popup menu
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.ui.EntrantEventDetailsActivity;

import java.util.List;

public class EntrantEventAdapter extends RecyclerView.Adapter<EntrantEventAdapter.ViewHolder> {
    private List<Event> events;
    private android.content.Context context;

    public EntrantEventAdapter(android.content.Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        if (event != null && event.getName() != null) {
            holder.name.setText(event.getName());
        } else {
            holder.name.setText("Unnamed Event");
        }

        // --- NEW: 3-DOTS CLICK LOGIC ---
        // -------------------------------
// Inside onBindViewHolder
        if (holder.ivEventOptions != null) {
            holder.ivEventOptions.setOnClickListener(v -> {
                // 1. THE TRUTH TEST
                android.widget.Toast.makeText(v.getContext(), "DEBUG: Dots clicked!", android.widget.Toast.LENGTH_SHORT).show();

                // 2. The Menu Logic
                android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
                popup.getMenu().add("Event Details");

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Event Details")) {
                        android.content.Intent intent = new android.content.Intent(v.getContext(), EntrantEventDetailsActivity.class);
                        intent.putExtra("EVENT_NAME", event.getName());
                        v.getContext().startActivity(intent);
                        return true;
                    }
                    return false;
                });

                popup.show();
            });
        }
        if (holder.btnJoin != null && holder.badge != null) {
            holder.btnJoin.setOnClickListener(v -> {
                String currentText = holder.btnJoin.getText().toString();
                if (currentText.equalsIgnoreCase("Join")) {
                    holder.btnJoin.setText("Leave");
                    holder.badge.setVisibility(View.VISIBLE);
                } else {
                    holder.btnJoin.setText("Join");
                    holder.badge.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public int getItemCount() { return events.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, badge;
        Button btnJoin;
        ImageButton ivEventOptions; // Added this variable

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            badge = itemView.findViewById(R.id.tvJoinedBadge);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            // Linked to the ID we put in your XML earlier
            ivEventOptions = itemView.findViewById(R.id.btnThreeDotsMenu);
        }
    }
}