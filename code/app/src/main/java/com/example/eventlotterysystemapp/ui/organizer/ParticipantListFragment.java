package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.NotificationManager;
import com.example.eventlotterysystemapp.data.models.Participant;
import com.example.eventlotterysystemapp.data.models.ParticipantAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fragment that lists participants.
 * Handles lottery drawing logic only when the 'waitlist' status is active.
 * Handles replacement drawing when the 'cancelled' status is active (US 01.05.01 / 02.05.03).
 */
public class ParticipantListFragment extends Fragment {
    private String eventId, status;
    private String maxLimit = "Unlimited";
    private RecyclerView recyclerView;
    private ParticipantAdapter adapter;
    private List<Participant> participantList = new ArrayList<>();
    private Button lotteryBtn;
    private Button drawReplacementBtn;
    private TextView tvCapacity;

    public static ParticipantListFragment newInstance(String eId, String stat) {
        ParticipantListFragment fragment = new ParticipantListFragment();
        Bundle args = new Bundle();
        args.putString("eId", eId);
        args.putString("stat", stat);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_participant_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString("eId");
            status = getArguments().getString("stat");
        }

        tvCapacity = view.findViewById(R.id.tvCapacity);
        lotteryBtn = view.findViewById(R.id.btnLottery);
        drawReplacementBtn = view.findViewById(R.id.btnDrawReplacement);
        recyclerView = view.findViewById(R.id.participantRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        boolean cancelBtn = "selected".equals(status);
        adapter = new ParticipantAdapter(participantList, cancelBtn, eventId);
        recyclerView.setAdapter(adapter);

        setupTabLogic();
        listenForParticipants();
    }

    private void setupTabLogic() {
        if ("waitlist".equals(status)) {
            lotteryBtn.setVisibility(View.VISIBLE);
            drawReplacementBtn.setVisibility(View.GONE);
            tvCapacity.setVisibility(View.VISIBLE);
            fetchEventCapacity();
            lotteryBtn.setOnClickListener(v -> runLottery());

        } else if ("cancelled".equals(status)) {
            // Show Draw Replacement button on the cancelled tab
            lotteryBtn.setVisibility(View.GONE);
            drawReplacementBtn.setVisibility(View.VISIBLE);
            tvCapacity.setVisibility(View.GONE);
            drawReplacementBtn.setOnClickListener(v -> drawOneReplacement());

        } else if ("selected".equals(status) || "enrolled".equals(status)) {
            lotteryBtn.setVisibility(View.GONE);
            drawReplacementBtn.setVisibility(View.GONE);
            tvCapacity.setVisibility(View.VISIBLE);
            fetchEventCapacity();

        } else {
            lotteryBtn.setVisibility(View.GONE);
            drawReplacementBtn.setVisibility(View.GONE);
            tvCapacity.setVisibility(View.GONE);
        }
    }

    private void fetchEventCapacity() {
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        Long limit;

                        if ("waitlist".equals(status)) {
                            limit = documentSnapshot.getLong("listLimit");
                        } else {
                            limit = documentSnapshot.getLong("maxParticipants");
                        }

                        if (limit != null && limit != Integer.MAX_VALUE) {
                            maxLimit = limit.toString();
                        } else {
                            maxLimit = "Unlimited";
                        }

                        updateCapacityText();
                    }
                });
    }

    private void updateCapacityText() {
        if ("waitlist".equals(status)) {
            tvCapacity.setText("Waitlist: " + participantList.size() + " / " + maxLimit);
        } else if ("selected".equals(status) || "enrolled".equals(status)) {
            tvCapacity.setText("Occupancy: " + participantList.size() + " / " + maxLimit);
        } else {
            tvCapacity.setText("Total: " + participantList.size());
        }
    }

    private void listenForParticipants() {
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId).collection("participants")
                .whereEqualTo("status", status)
                .addSnapshotListener((querySnap, e) -> {
                    if (e != null || !isAdded()) return;
                    participantList.clear();
                    if (querySnap != null) {
                        for (QueryDocumentSnapshot doc : querySnap) {
                            Participant p = doc.toObject(Participant.class);
                            participantList.add(p);
                        }
                        adapter.notifyDataSetChanged();

                        if ("selected".equals(status)) {
                            calculateTotalOccupancy();
                        } else if ("enrolled".equals(status)) {
                            calculateTotalOccupancyForEnrolled();
                        } else {
                            updateCapacityText();
                        }
                    }
                });
    }

    private void calculateTotalOccupancy() {
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId).collection("participants")
                .whereEqualTo("status", "enrolled")
                .get()
                .addOnSuccessListener(enrolledSnap -> {
                    if (!isAdded()) return;
                    int totalOccupancy = enrolledSnap.size() + participantList.size();
                    tvCapacity.setText("Occupancy: " + totalOccupancy + " / " + maxLimit);
                });
    }

    private void calculateTotalOccupancyForEnrolled() {
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId).collection("participants")
                .whereEqualTo("status", "selected")
                .get()
                .addOnSuccessListener(selectedSnap -> {
                    if (!isAdded()) return;
                    int totalOccupancy = selectedSnap.size() + participantList.size();
                    tvCapacity.setText("Occupancy: " + totalOccupancy + " / " + maxLimit);
                });
    }

    /**
     * Draws one random replacement from the waitlist and promotes them to selected.
     * Satisfies US 01.05.01 and US 02.05.03.
     */
    private void drawOneReplacement() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            if (!eventDoc.exists() || !isAdded()) return;

            String eventName = eventDoc.getString("name");

            // Fetch everyone currently on the waitlist
            db.collection("events").document(eventId).collection("participants")
                    .whereEqualTo("status", "waitlist")
                    .get()
                    .addOnSuccessListener(waitlistSnap -> {
                        if (!isAdded()) return;

                        if (waitlistSnap.isEmpty()) {
                            Toast.makeText(getContext(), "No one left on the waitlist!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Pick one random person from the waitlist
                        List<QueryDocumentSnapshot> waitlistDocs = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : waitlistSnap) {
                            waitlistDocs.add(doc);
                        }
                        Collections.shuffle(waitlistDocs);
                        QueryDocumentSnapshot chosen = waitlistDocs.get(0);

                        String chosenEmail = chosen.getId();

                        // Promote them to selected
                        db.collection("events").document(eventId)
                                .collection("participants").document(chosenEmail)
                                .update("status", "selected")
                                .addOnSuccessListener(aVoid -> {
                                    if (!isAdded()) return;

                                    // Notify the replacement winner
                                    NotificationManager nm = new NotificationManager();
                                    nm.sendNotification(chosenEmail, chosenEmail,
                                            "Great news! A spot opened up and you have been selected for " + eventName + "!",
                                            "LOTTERY_WIN", eventId);

                                    Toast.makeText(getContext(), "Replacement drawn: " + chosenEmail, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Failed to draw replacement", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to load waitlist", Toast.LENGTH_SHORT).show());
        });
    }

    /**
     * Runs the full lottery drawing from the waitlist.
     */
    private void runLottery() {
        if (participantList.isEmpty()) {
            Toast.makeText(getContext(), "Waitlist is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            if (!eventDoc.exists()) return;

            String eventName = eventDoc.getString("name");
            Long maxParticipantsLong = eventDoc.getLong("maxParticipants");
            int totalCapacity = (maxParticipantsLong != null) ? maxParticipantsLong.intValue() : Integer.MAX_VALUE;

            db.collection("events").document(eventId).collection("participants")
                    .get()
                    .addOnSuccessListener(allParticipants -> {
                        int currentOccupancy = 0;
                        for (QueryDocumentSnapshot doc : allParticipants) {
                            String s = doc.getString("status");
                            if ("selected".equals(s) || "enrolled".equals(s)) currentOccupancy++;
                        }

                        int spotsAvailable = totalCapacity - currentOccupancy;
                        if (spotsAvailable <= 0) {
                            Toast.makeText(getContext(), "Event is already full!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Participant> shuffledList = new ArrayList<>(participantList);
                        Collections.shuffle(shuffledList);

                        int actualWinnersCount = Math.min(spotsAvailable, shuffledList.size());
                        WriteBatch batch = db.batch();
                        NotificationManager nm = new NotificationManager();

                        for (int i = 0; i < shuffledList.size(); i++) {
                            Participant p = shuffledList.get(i);
                            DocumentReference pRef = db.collection("events")
                                    .document(eventId).collection("participants").document(p.getEmail());

                            if (i < actualWinnersCount) {
                                batch.update(pRef, "status", "selected");
                                nm.sendNotification(p.getEmail(), p.getEmail(),
                                        "Congratulations! You won the lottery for " + eventName + ".", "LOTTERY_WIN", eventId);
                            } else {
                                nm.sendNotification(p.getEmail(), p.getEmail(),
                                        "You were not selected for " + eventName + " this time, but you are still on the waitlist!",
                                        "LOTTERY_LOSS", eventId);
                            }
                        }

                        batch.commit().addOnSuccessListener(aVoid -> {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Lottery complete!", Toast.LENGTH_SHORT).show();
                                lotteryBtn.setEnabled(false);
                                lotteryBtn.setText("Lottery Completed");
                            }
                        });
                    });
        });
    }
}