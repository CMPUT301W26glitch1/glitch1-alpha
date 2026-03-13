package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.EventController;

public class CreateEventActivity extends AppCompatActivity {

    EditText eventTitle;
    EditText eventDescription;
    EditText eventCategory;
    EditText eventTime;
    EditText eventPlace;
    EditText registrationStart;
    EditText registrationEnd;
    EditText listLimit;

    Button nextBtn;

    EventController eventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Connect layout views
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        eventCategory = findViewById(R.id.eventCategory);
        eventTime = findViewById(R.id.eventTime);
        eventPlace = findViewById(R.id.eventPlace);
        registrationStart = findViewById(R.id.registrationStart);
        registrationEnd = findViewById(R.id.registrationEnd);
        listLimit = findViewById(R.id.listLimit);

        nextBtn = findViewById(R.id.nextBtn);

        // Initialize controller
        eventController = new EventController(this);

        nextBtn.setOnClickListener(v -> {

            String title = eventTitle.getText().toString();
            String category = eventCategory.getText().toString();

            EventController.EventData event =
                    new EventController.EventData(title, category, "Open","Default");

            eventController.addEvent(event);

            Toast.makeText(CreateEventActivity.this,
                    "Event added successfully!", Toast.LENGTH_SHORT).show();

            eventTitle.setText("");
            eventDescription.setText("");
            eventCategory.setText("");
        });
    }
}