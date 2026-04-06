package com.example.eventlotterysystemapp.ui.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.Calendar;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

/**
 * Screen for organizer to create an event.
 * Updated: Resolves Email to Randomized User ID before saving to Firestore.
 */
public class CreateEventActivity extends AppCompatActivity {
    private String organizerEmail;
    private EditText eventTitle, eventDescription, category, eventTime, regStart, regEnd, eventPlace, listLimit, maxParticipants;
    private Switch geoSwitch, eventSwitch;
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
        AccessibilityUtils.applyAccessibilityMode(this);

        organizerEmail = getIntent().getStringExtra("USER_EMAIL");
        eventController = new EventController(this);

        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        category = findViewById(R.id.eventCategory);
        eventTime = findViewById(R.id.eventTime);
        regStart = findViewById(R.id.registrationStart);
        regEnd = findViewById(R.id.registrationEnd);
        eventPlace = findViewById(R.id.eventPlace);
        geoSwitch = findViewById(R.id.geoLocation);
        eventSwitch = findViewById(R.id.privateEventSwitch);
        maxParticipants = findViewById(R.id.maxParticipants);
        listLimit = findViewById(R.id.listLimit);
        nextBtn = findViewById(R.id.nextBtn);
        eventPoster = findViewById(R.id.eventPoster);
        selectImageBtn = findViewById(R.id.selectImageBtn);

        selectImageBtn.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        setupDateTimePicker(eventTime, dt -> selectedEventTime = dt);
        setupDateTimePicker(regStart, dt -> selectedRegStart = dt);
        setupDateTimePicker(regEnd, dt -> selectedRegEnd = dt);

        nextBtn.setOnClickListener(v -> saveEvent());
    }

    /**
     * Finds the randomized User ID based on email, then saves the event.
     */
    private void saveEvent() {
        if (organizerEmail == null) {
            Toast.makeText(this, "Organizer email missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", organizerEmail)
                .get()
                .addOnSuccessListener(userQuery -> {
                    if (!userQuery.isEmpty()) {
                        String actualUserDocId = userQuery.getDocuments().get(0).getId();
                        proceedWithSave(actualUserDocId);
                    } else {
                        Toast.makeText(this, "Could not find user profile in database.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Logic for creating the Event object and handling Firestore/Storage.
     */
    private void proceedWithSave(String organizerId) {
        String limitStr = listLimit.getText().toString().trim();
        int waitlistLimitTemp = limitStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(limitStr);

        String maxPartsStr = maxParticipants.getText().toString().trim();
        int maxParticipantsTemp;
        if (maxPartsStr.isEmpty()) {
            maxParticipantsTemp = Integer.MAX_VALUE;
        } else {
            try {
                maxParticipantsTemp = Integer.parseInt(maxPartsStr);
            } catch (NumberFormatException e) {
                maxParticipantsTemp = Integer.MAX_VALUE;
            }
        }

        final int finalWaitlistLimit = waitlistLimitTemp;
        final int finalMaxParticipants = maxParticipantsTemp;
        final boolean isPrivate = eventSwitch.isChecked();

        Event event = new Event(
                eventTitle.getText().toString(),
                eventDescription.getText().toString(),
                category.getText().toString(),
                eventPlace.getText().toString(),
                selectedEventTime,
                selectedRegStart,
                selectedRegEnd,
                geoSwitch.isChecked(),
                organizerId,
                null,
                finalWaitlistLimit,
                finalMaxParticipants,
                isPrivate
        );

        eventController.addEvent(event, docRef -> {
            String eventId = docRef.getId();
            event.setEventId(eventId);

            docRef.update("eventId", eventId);
            docRef.update("privateEvent", isPrivate);
            docRef.update("listLimit", finalWaitlistLimit);
            docRef.update("maxParticipants", finalMaxParticipants);

            if (selectedImageUri != null) {
                StorageController storageController = new StorageController();
                storageController.uploadPoster(eventId, selectedImageUri, downloadUrl -> {
                    docRef.update("posterUrl", downloadUrl)
                            .addOnSuccessListener(aVoid -> navigateToQR(eventId, isPrivate))
                            .addOnFailureListener(e -> navigateToQR(eventId, isPrivate));
                });
            } else {
                navigateToQR(eventId, isPrivate);
            }
        });
    }

    private void navigateToQR(String eventId, boolean isPrivate) {
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        intent.putExtra("IS_PRIVATE", isPrivate);
        startActivity(intent);
        finish();
    }

    private void setupDateTimePicker(EditText editText, DateTimeCallback callback) {
        editText.setFocusable(false);
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