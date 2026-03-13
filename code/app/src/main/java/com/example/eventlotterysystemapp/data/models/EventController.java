package com.example.eventlotterysystemapp.data.models;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventController {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Context context;

    public EventController(Context context) {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        this.context = context;
    }

    // Add a new event to Firestore
    public void addEvent(EventData event) {
        eventsRef.add(event)
                .addOnSuccessListener(docRef ->
                        Toast.makeText(context, "Event added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show());
    }

    // Update a field of an existing event
    public void updateEvent(String eventId, String field, String value) {
        eventsRef.document(eventId)
                .update(field, value)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Event updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update event", Toast.LENGTH_SHORT).show());
    }

    // Get full events for filtering/search
    public void getAllEventObjects(@NonNull EventCallback callback) {
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<EventData> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String id = doc.getId();
                    String name = doc.contains("name") ? doc.getString("name") : "Unnamed Event";
                    String category = doc.contains("category") ? doc.getString("category") : "Other";
                    String availability = doc.contains("availability") ? doc.getString("availability") : "Open";
                    events.add(new EventData(id, name, category, availability));
                }
                callback.onSuccess(events);
            } else {
                callback.onError("Failed to load events");
            }
        });
    }

    // Callback interface
    public interface EventCallback {
        void onSuccess(ArrayList<EventData> events);
        void onError(String message);
    }

    // Event object including ID
    public static class EventData {
        public String id;
        public String name;
        public String category;
        public String availability;

        public EventData(String id, String name, String category, String availability) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.availability = availability;
        }
    }

    // Optional: simple helper to populate names only
    public void getAllEventNames(ArrayList<String> names, ArrayAdapter<String> adapter) {
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                names.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String name = doc.contains("name") ? doc.getString("name") : "Unnamed Event";
                    names.add(name);
                }
                if (adapter != null) adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Failed to load event names", Toast.LENGTH_SHORT).show();
            }
        });
    }
}