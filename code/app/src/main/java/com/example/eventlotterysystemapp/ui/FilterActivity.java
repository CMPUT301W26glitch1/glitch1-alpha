package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventlotterysystemapp.R;

public class FilterActivity extends AppCompatActivity {

    private Spinner spinnerAvailability, spinnerInterests;
    private Button btnReset, btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        spinnerAvailability = findViewById(R.id.spinnerAvailability);
        spinnerInterests = findViewById(R.id.spinnerInterests);
        btnReset = findViewById(R.id.btnReset);
        btnApply = findViewById(R.id.btnApplyFilter);

        // Fill spinners with options
        String[] availOptions = {"Availability", "Open", "Full"};
        String[] interestOptions = {"Interests", "Music", "Tech", "Art", "Nature"};

        spinnerAvailability.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availOptions));
        spinnerInterests.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, interestOptions));

        btnApply.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("AVAIL", spinnerAvailability.getSelectedItem().toString());
            resultIntent.putExtra("INTEREST", spinnerInterests.getSelectedItem().toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnReset.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}