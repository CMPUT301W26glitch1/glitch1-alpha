package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventlotterysystemapp.R;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class LotteryInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_info);
        AccessibilityUtils.applyAccessibilityMode(this);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}