package com.example.eventlotterysystemapp.ui.organizer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


/**
 * Generates and displays the QR Code for an event
 */
public class QRCodeActivity extends AppCompatActivity {

    private ImageView imageQR;
    private Button returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        AccessibilityUtils.applyAccessibilityMode(this);

        imageQR = findViewById(R.id.imageQR);
        returnBtn = findViewById(R.id.returnBtn);

        if (AccessibilityUtils.isAccessibilityModeOn(this)) {
            returnBtn.setTextSize(16);
            returnBtn.setMinHeight(90);
        }

        String eventId = getIntent().getStringExtra("EVENT_ID");
        boolean isPrivate = getIntent().getBooleanExtra("IS_PRIVATE", false);

        if (eventId != null && !isPrivate) {
            generateQRCode("Event_ID:" + eventId);
        }

        returnBtn.setOnClickListener(v -> finish());
    }

    /**
     * Generates a QR Code for an event
     * @param text the text to be encoded into the QR Code, i.e: the eventId
     */
    private void generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            // Encode the eventId into a BitMatrix (200x200)
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // Convert BitMatrix to Bitmap pixels
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageQR.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}