package com.example.eventlotterysystemapp.ui.organizer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * View to update the poster image for an event
 */
public class UpdatePosterActivity extends AppCompatActivity {
    private ImageView previewPoster;
    private Button newImgBtn, updateBtn, returnBtn;
    private Uri selectedImageUri;
    private String eventId;

    // Use ActivityResultLauncher to handle image selection
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    previewPoster.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_poster);
        AccessibilityUtils.applyAccessibilityMode(this);

        eventId = getIntent().getStringExtra("EVENT_ID");

        previewPoster = findViewById(R.id.previewPoster);
        newImgBtn = findViewById(R.id.newImgBtn);
        updateBtn = findViewById(R.id.updateBtn);
        returnBtn = findViewById(R.id.returnToMenu);

        loadExistingPoster();

        newImgBtn.setOnClickListener(v -> mGetContent.launch("image/*"));

        updateBtn.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageToFirebase();
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });

        returnBtn.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Uploads the updated poster image to the firestore database
     */
    private void uploadImageToFirebase() {
        // Create a unique path for the poster based on the eventId
        StorageReference ref = FirebaseStorage.getInstance().getReference("posters/" + eventId);

        ref.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                // Update the specific event document in Firestore with the new image URL
                FirebaseFirestore.getInstance().collection("events")
                        .document(eventId)
                        .update("posterUrl", uri.toString())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Poster updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show());
            });
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Loads the current event poster from firestore database to be displayed for preview when the view is first opened
     */
    private void loadExistingPoster() {
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String currentUrl = documentSnapshot.getString("posterUrl");
                    if (currentUrl != null && !currentUrl.isEmpty()) {
                        // Glide handles the network request and decoding automatically
                        Glide.with(this)
                                .load(currentUrl)
                                .into(previewPoster);
                    }
                });
    }
}