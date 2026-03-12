package com.example.eventlotterysystemapp.data.models;

import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.function.Consumer;
public class StorageController {
    private StorageReference storageRef;

    public StorageController() {
        storageRef = FirebaseStorage.getInstance().getReference("event_posters");
    }

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
