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
 * Screen for organizer to create an event
 */
public class CreateEventActivity extends AppCompatActivity {
    private String organizerEmail;
    private EditText eventTitle, eventDescription, category, eventTime, regStart, regEnd, eventPlace, listLimit;
    private Switch geoSwitch;
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
        listLimit = findViewById(R.id.listLimit); // Reference to the capacity field
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

    private void saveEvent() {
        // Logic to handle the participant limit
        String limitStr = listLimit.getText().toString().trim();
        int finalLimit;

        if (limitStr.isEmpty()) {
            // No value entered = Unlimited
            finalLimit = Integer.MAX_VALUE;
        } else {
            try {
                finalLimit = Integer.parseInt(limitStr);
            } catch (NumberFormatException e) {
                // Fallback for invalid input
                finalLimit = Integer.MAX_VALUE;
            }
        }

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
                null,
                finalLimit // Passing the limit to the updated constructor
        );

        eventController.addEvent(event, docRef -> {
            String eventId = docRef.getId();
            event.setEventId(eventId);
            docRef.update("eventId", eventId);

            if (selectedImageUri != null) {
                StorageController storageController = new StorageController();
                storageController.uploadPoster(eventId, selectedImageUri, downloadUrl -> {
                    docRef.update("posterUrl", downloadUrl);
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

    private void setupDateTimePicker(EditText editText, DateTimeCallback callback) {
        // Prevent keyboard from opening on date/time fields
        editText.setFocusable(false);
        editText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                new TimePickerDialog(this, (view1, hour, minute) -> {
                    LocalDateTime ldt = LocalDateTime.of(year, month + 1, day, hour, minute);
                    // Simple formatting for visibility
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