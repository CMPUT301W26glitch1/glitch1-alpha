package com.example.eventlotterysystemapp.data.models;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void getAllEvents(ArrayList<String> eventsList, ArrayAdapter<String> adapter) {
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventsList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String eventName = doc.getString("name");
                    eventsList.add(eventName);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}