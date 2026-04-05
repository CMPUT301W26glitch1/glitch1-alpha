package com.example.eventlotterysystemapp.ui.organizer;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.ParticipantPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;

public class EventParticipantsActivity extends AppCompatActivity {

    private String eventId;
    private FirebaseFirestore db;
    private int remainingUsers;
    private ArrayList<String> csvRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        eventId = getIntent().getStringExtra("EVENT_ID");
        db = FirebaseFirestore.getInstance();

        // Initialize Buttons
        Button btnReturn = findViewById(R.id.btnReturn);
        Button btnNotifyTop = findViewById(R.id.btnNotifyTop);
        Button btnExportCSV = findViewById(R.id.btnExportCsv);

        // Simple finish() to go back to OrganizerMainActivity
        btnReturn.setOnClickListener(v -> finish());

        btnNotifyTop.setOnClickListener(v -> {
            Intent intent = new Intent(EventParticipantsActivity.this, NotificationActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });

        btnExportCSV.setOnClickListener(v -> exportEnrolledCsv());

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ParticipantPagerAdapter adapter = new ParticipantPagerAdapter(this, eventId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch(position) {
                case 0: tab.setText("Waitlist"); break;
                case 1: tab.setText("Selected"); break;
                case 2: tab.setText("Cancelled"); break;
                case 3: tab.setText("Enrolled"); break;
            }
        }).attach();
    }

    private void exportEnrolledCsv() {
        db.collection("events")
                .document(eventId)
                .collection("participants")
                .whereEqualTo("status", "enrolled")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "No enrolled participants found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    csvRows = new ArrayList<>();
                    csvRows.add("Name,Email,Status");

                    remainingUsers = querySnapshot.getDocuments().size();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String email = doc.getId();
                        String status = doc.getString("status");

                        db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    String name = "Unknown User";

                                    if (!userSnapshot.isEmpty()) {
                                        String loadedName = userSnapshot.getDocuments().get(0).getString("name");
                                        if (loadedName != null && !loadedName.isEmpty()) {
                                            name = loadedName;
                                        }
                                    }

                                    csvRows.add(escapeCsv(name) + "," + escapeCsv(email) + "," + escapeCsv(status));
                                    remainingUsers--;

                                    if (remainingUsers == 0) {
                                        saveCsvFile();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    csvRows.add(escapeCsv("Unknown User") + "," + escapeCsv(email) + "," + escapeCsv(status));
                                    remainingUsers--;

                                    if (remainingUsers == 0) {
                                        saveCsvFile();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load enrolled participants", Toast.LENGTH_SHORT).show());
    }

    private void saveCsvFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "enrolled_participants_" + timeStamp + ".csv";

            File folder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (folder == null) {
                Toast.makeText(this, "Could not access export folder", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(folder, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            for (String row : csvRows) {
                writer.append(row).append("\n");
            }

            writer.flush();
            writer.close();
            fos.close();

            Toast.makeText(this, "CSV exported: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to export CSV", Toast.LENGTH_SHORT).show();
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}