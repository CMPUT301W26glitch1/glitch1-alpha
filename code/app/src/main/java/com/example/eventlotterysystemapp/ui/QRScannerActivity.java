package com.example.eventlotterysystemapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan an event QR code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String scanned = result.getContents();
                if (scanned.startsWith("Event_ID:")) {
                    String eventId = scanned.replace("Event_ID:", "").trim();
                    Intent intent = new Intent(this, EntrantEventDetailsActivity.class);
                    intent.putExtra("EVENT_ID", eventId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            finish();
        }
    }
}