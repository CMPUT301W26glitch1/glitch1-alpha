package com.example.eventlotterysystemapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;

import java.util.List;

public class OrganizerAdapter extends RecyclerView.Adapter<OrganizerAdapter.ViewHolder> {

    public static class OrganizerItem {
        public String id;
        public String name;
        public String email;

        public OrganizerItem(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

    public interface OnRemoveClickListener {
        void onRemove(OrganizerItem organizer);
    }

    private final List<OrganizerItem> organizers;
    private final OnRemoveClickListener listener;

    public OrganizerAdapter(List<OrganizerItem> organizers, OnRemoveClickListener listener) {
        this.organizers = organizers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organizer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrganizerItem organizer = organizers.get(position);

        holder.name.setText(organizer.name);
        holder.email.setText(organizer.email);

        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(organizer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return organizers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            email = itemView.findViewById(R.id.textEmail);
            removeButton = itemView.findViewById(R.id.btnRemove);
        }
    }
}