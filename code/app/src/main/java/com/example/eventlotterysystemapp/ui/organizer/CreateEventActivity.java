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

public class CreateEventActivity extends AppCompatActivity {
    private EditText eventTitle, eventDescription, category, eventTime, regStart, regEnd, eventPlace;
    private Switch geoSwitch;
    private Button nextBtn;
    private EventController eventController;
    private ImageView eventPoster;
    private Button selectImageBtn;
    private Uri selectedImageUri; // Stores the local image path

    // Store selected times
    private LocalDateTime selectedEventTime, selectedRegStart, selectedRegEnd;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    // User selected an image, set it to your ImageView
                    eventPoster.setImageURI(uri);
                    // Store the URI in a variable so you can upload it later
                    selectedImageUri = uri;
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventController = new EventController();

        // Bind views
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        category = findViewById(R.id.eventCategory);
        eventTime = findViewById(R.id.eventTime);
        regStart = findViewById(R.id.registrationStart);
        regEnd = findViewById(R.id.registrationEnd);
        eventPlace = findViewById(R.id.eventPlace);
        geoSwitch = findViewById(R.id.geoLocation);
        nextBtn = findViewById(R.id.nextBtn);
        eventPoster = findViewById(R.id.eventPoster);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        selectImageBtn.setOnClickListener(v -> {
            // Launch the photo picker
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Set up pickers
        setupDateTimePicker(eventTime, dt -> selectedEventTime = dt);
        setupDateTimePicker(regStart, dt -> selectedRegStart = dt);
        setupDateTimePicker(regEnd, dt -> selectedRegEnd = dt);

        nextBtn.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Event event = new Event(
                eventTitle.getText().toString(),
                eventDescription.getText().toString(),
                category.getText().toString(),
                eventPlace.getText().toString(),
                selectedEventTime,
                selectedRegStart,
                selectedRegEnd,
                geoSwitch.isChecked(),
                deviceId,
                null // Poster URL is null here, updated after upload
        );

        // Save Event to Firestore first
        eventController.addEvent(event, docRef -> {
            String eventId = docRef.getId();

            event.setEventId(eventId);
            docRef.update("eventId", eventId);

            // If an image was selected, upload it
            if (selectedImageUri != null) {
                StorageController storageController = new StorageController();
                storageController.uploadPoster(eventId, selectedImageUri, downloadUrl -> {
                    // 3. Once uploaded, update the event with the photo URL
                    docRef.update("posterUrl", downloadUrl);

                    // Now navigate
                    navigateToQR(eventId);
                });
            } else {
                navigateToQR(eventId);
            }
        });
    }


    private void navigateToQR(String eventId) {
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
        finish();
    }

    // Helper to open Date and Time pickers sequentially
    private void setupDateTimePicker(EditText editText, DateTimeCallback callback) {
        editText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                new TimePickerDialog(this, (view1, hour, minute) -> {
                    LocalDateTime ldt = LocalDateTime.of(year, month + 1, day, hour, minute);
                    editText.setText(ldt.toString());
                    callback.onDateTimeSelected(ldt);
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private interface DateTimeCallback {
        void onDateTimeSelected(LocalDateTime ldt);
    }
}
