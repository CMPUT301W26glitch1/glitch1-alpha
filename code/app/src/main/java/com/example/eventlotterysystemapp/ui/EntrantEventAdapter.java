package com.example.eventlotterysystemapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import java.util.List;

public class EntrantEventAdapter extends RecyclerView.Adapter<EntrantEventAdapter.ViewHolder> {
    private List<Event> events;

    public EntrantEventAdapter(List<Event> events) { this.events = events; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        // Safety check: only set text if name isn't null
        if (event != null && event.getName() != null) {
            holder.name.setText(event.getName());
        } else {
            holder.name.setText("Unnamed Event");
        }

        // Ensure the button and badge aren't null before acting on them
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
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            badge = itemView.findViewById(R.id.tvJoinedBadge);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }
    }
}