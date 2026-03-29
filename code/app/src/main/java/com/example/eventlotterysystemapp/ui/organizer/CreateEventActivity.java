package com.example.eventlotterysystemapp.ui.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.EventController;
import com.example.eventlotterysystemapp.data.models.StorageController;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Screen for organizer to create an event.
 * Merged version: Includes Participant Limits and Private Event status.
 */
public class CreateEventActivity extends AppCompatActivity {
    private String organizerEmail;
    private EditText eventTitle, eventDescription, category, eventTime, regStart, regEnd, eventPlace, listLimit;
    private Switch geoSwitch, eventSwitch; // Both switches merged here
    private Button nextBtn;
    private EventController eventController;
    private ImageView eventPoster;
    private Button selectImageBtn;
    private Uri selectedImageUri;

    private LocalDateTime selectedEventTime, selectedRegStart, selectedRegEnd;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    eventPoster.setImageURI(uri);
                    selectedImageUri = uri;
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        organizerEmail = getIntent().getStringExtra("USER_EMAIL");
        eventController = new EventController(this);

        // Bind all merged views
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        category = findViewById(R.id.eventCategory);
        eventTime = findViewById(R.id.eventTime);
        regStart = findViewById(R.id.registrationStart);
        regEnd = findViewById(R.id.registrationEnd);
        eventPlace = findViewById(R.id.eventPlace);
        geoSwitch = findViewById(R.id.geoLocation);
        eventSwitch = findViewById(R.id.privateEventSwitch); // From Member branch
        listLimit = findViewById(R.id.listLimit);           // From Your branch
        nextBtn = findViewById(R.id.nextBtn);
        eventPoster = findViewById(R.id.eventPoster);
        selectImageBtn = findViewById(R.id.selectImageBtn);

        selectImageBtn.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        setupDateTimePicker(eventTime, dt -> selectedEventTime = dt);
        setupDateTimePicker(regStart, dt -> selectedRegStart = dt);
        setupDateTimePicker(regEnd, dt -> selectedRegEnd = dt);

        nextBtn.setOnClickListener(v -> saveEvent());
    }

    /**
     * Saves event to Firestore, handles image upload, limits, and privacy settings.
     */
    private void saveEvent() {
        // Logic to handle the participant limit
        String limitStr = listLimit.getText().toString().trim();
        int finalLimit;

        if (limitStr.isEmpty()) {
            finalLimit = Integer.MAX_VALUE; // Unlimited
        } else {
            try {
                finalLimit = Integer.parseInt(limitStr);
            } catch (NumberFormatException e) {
                finalLimit = Integer.MAX_VALUE;
            }
        }

        final int finalLimitToSave = finalLimit;
        boolean isPrivate = eventSwitch.isChecked();

        // Create Event Object
        Event event = new Event(
                eventTitle.getText().toString(),
                eventDescription.getText().toString(),
                category.getText().toString(),
                eventPlace.getText().toString(),
                selectedEventTime,
                selectedRegStart,
                selectedRegEnd,
                geoSwitch.isChecked(),
                organizerEmail,
                null, // posterUrl starts as null
                finalLimitToSave,
                isPrivate
        );

        // Save to Firestore
        eventController.addEvent(event, docRef -> {
            String eventId = docRef.getId();
            event.setEventId(eventId);

            // Update document with its own ID and the extra fields
            docRef.update("eventId", eventId);
            docRef.update("privateEvent", isPrivate);
            docRef.update("listLimit", finalLimitToSave);

            // Handle Image Upload if a poster was selected
            if (selectedImageUri != null) {
                StorageController storageController = new StorageController();
                storageController.uploadPoster(eventId, selectedImageUri, downloadUrl -> {
                    // Update Firestore with the new download URL from Firebase Storage
                    docRef.update("posterUrl", downloadUrl)
                            .addOnSuccessListener(aVoid -> navigateToQR(eventId))
                            .addOnFailureListener(e -> {
                                Toast.makeText(CreateEventActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                navigateToQR(eventId);
                            });
                });
            } else {
                navigateToQR(eventId);
            }
        });
    }

    /**
     * Helper to handle navigation logic based on privacy status.
     */
    private void handleNavigation(String eventId, boolean isPrivate) {
        if (isPrivate) {
            finish();
        } else {
            navigateToQR(eventId);
        }
    }

    private void navigateToQR(String eventId) {
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
        finish();
    }

    private void setupDateTimePicker(EditText editText, DateTimeCallback callback) {
        editText.setFocusable(false); // Added to ensure better UX
        editText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                new TimePickerDialog(this, (view1, hour, minute) -> {
                    LocalDateTime ldt = LocalDateTime.of(year, month + 1, day, hour, minute);
                    editText.setText(year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute);
                    callback.onDateTimeSelected(ldt);
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private interface DateTimeCallback {
        void onDateTimeSelected(LocalDateTime ldt);
    }
}