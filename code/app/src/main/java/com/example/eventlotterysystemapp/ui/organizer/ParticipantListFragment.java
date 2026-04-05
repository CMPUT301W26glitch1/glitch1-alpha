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
 */
public class ParticipantListFragment extends Fragment {
    private String eventId, status;
    private String maxLimit = "Unlimited";
    private RecyclerView recyclerView;
    private ParticipantAdapter adapter;
    private List<Participant> participantList = new ArrayList<>();
    private Button lotteryBtn;
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
        recyclerView = view.findViewById(R.id.participantRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        boolean cancelBtn = false;
        if (status.equals("selected")) {
            cancelBtn = true;
        }
        adapter = new ParticipantAdapter(participantList, cancelBtn, eventId);
        recyclerView.setAdapter(adapter);

        setupTabLogic();
        listenForParticipants();
    }

    private void setupTabLogic() {
        if ("waitlist".equals(status)) {
            lotteryBtn.setVisibility(View.VISIBLE);
            tvCapacity.setVisibility(View.VISIBLE);
            fetchEventCapacity();

            // Pass 0 because the method now calculates winners internally
            lotteryBtn.setOnClickListener(v -> runLottery(0));

        } else if ("selected".equals(status) || "enrolled".equals(status)) {
            lotteryBtn.setVisibility(View.GONE);
            tvCapacity.setVisibility(View.VISIBLE);
            fetchEventCapacity();
        } else {
            lotteryBtn.setVisibility(View.GONE);
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
        } else {
            tvCapacity.setText("Count: " + participantList.size());
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

                    int enrolledCount = enrolledSnap.size();
                    int selectedCount = participantList.size();
                    int totalOccupancy = enrolledCount + selectedCount;

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

                    int selectedCount = selectedSnap.size();
                    int enrolledCount = participantList.size();
                    int totalOccupancy = selectedCount + enrolledCount;

                    tvCapacity.setText("Occupancy: " + totalOccupancy + " / " + maxLimit);
                });
    }

    private void runLottery(int ignore) {
        if (participantList.isEmpty()) {
            Toast.makeText(getContext(), "Waitlist is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            if (!eventDoc.exists()) return;

            Long maxParticipantsLong = eventDoc.getLong("maxParticipants");
            int totalCapacity = (maxParticipantsLong != null) ? maxParticipantsLong.intValue() : Integer.MAX_VALUE;

            db.collection("events").document(eventId).collection("participants")
                    .whereIn("status", java.util.Arrays.asList("selected", "enrolled"))
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        int currentOccupancy = querySnapshot.size();
                        int spotsAvailable = totalCapacity - currentOccupancy;

                        if (spotsAvailable <= 0) {
                            Toast.makeText(getContext(), "Event is already full!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Participant> shuffledList = new ArrayList<>(participantList);
                        Collections.shuffle(shuffledList);

                        int actualWinners = Math.min(spotsAvailable, shuffledList.size());

                        WriteBatch batch = db.batch();
                        for (int i = 0; i < actualWinners; i++) {
                            Participant winner = shuffledList.get(i);
                            DocumentReference ref = db.collection("events")
                                    .document(eventId)
                                    .collection("participants")
                                    .document(winner.getEmail());
                            batch.update(ref, "status", "selected");
                        }

                        batch.commit().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Lottery picked " + actualWinners + " participants!", Toast.LENGTH_SHORT).show();
                            lotteryBtn.setEnabled(false);
                            lotteryBtn.setText("Lottery Completed");
                        });
                    });
        });
    }
}