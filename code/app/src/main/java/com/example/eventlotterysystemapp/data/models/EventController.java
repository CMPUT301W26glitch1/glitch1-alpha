package com.example.eventlotterysystemapp.data.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EventController {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    public EventController() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
    }

    /**
     * Saves a new event to Firestore.
     * @param event The event object to save.
     * @param listener Callback to handle the document reference (and the new ID).
     */
    public void addEvent(Event event, OnSuccessListener<DocumentReference> listener) {
        eventsRef.add(event).addOnSuccessListener(listener);
    }

    /**
     * Retrieves all events created by a specific organizer.
     * @param organizerId The device ID of the organizer.
     * @param listener Callback to handle the list of events.
     */
    public void getOrganizerEvents(String organizerId, OnSuccessListener<QuerySnapshot> listener) {
        eventsRef.whereEqualTo("organizerId", organizerId).get().addOnSuccessListener(listener);
    }
}
