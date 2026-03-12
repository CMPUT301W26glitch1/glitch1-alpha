package com.example.eventlotterysystemapp.data.models;

import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.function.Consumer;

/**
 * Used to manage Firestore Storage
 */
public class StorageController {
    private StorageReference storageRef;

    public StorageController() {
        storageRef = FirebaseStorage.getInstance().getReference("event_posters");
    }

    /**
     * Uploads a poster image to firestore storage
     * @param eventId Id of the event to be linked to the poster image
     * @param imageUri The local Uri of the image file to be uploaded
     * @param onSuccess A Consumer functional interface that receives the public
     * download URL string once the upload and URL retrieval are complete.
     */
    public void uploadPoster(String eventId, Uri imageUri, Consumer<String> onSuccess) {
        // Create a unique path: event_posters/EVENT_ID.jpg
        StorageReference fileRef = storageRef.child(eventId + ".jpg");

        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // Get the public download URL
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                onSuccess.accept(uri.toString());
            });
        });
    }
}
