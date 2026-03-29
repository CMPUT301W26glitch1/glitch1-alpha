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
        adapter = new ParticipantAdapter(participantList);
        recyclerView.setAdapter(adapter);

        setupTabLogic();
        listenForParticipants();
    }

    private void setupTabLogic() {
        if ("waitlist".equals(status)) {
            lotteryBtn.setVisibility(View.VISIBLE);
            tvCapacity.setVisibility(View.VISIBLE);
            fetchEventCapacity();

            lotteryBtn.setOnClickListener(v -> {
                FirebaseFirestore.getInstance().collection("events")
                        .document(eventId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Long limit = documentSnapshot.getLong("listLimit");
                                int winnersToPick = (limit != null) ? limit.intValue() : Integer.MAX_VALUE;
                                runLottery(winnersToPick);
                            }
                        });
            });
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
                        Long limit = documentSnapshot.getLong("listLimit");
                        String limitText = (limit != null) ? limit.toString() : "Unlimited";
                        tvCapacity.setText("Max Participants: " + limitText);
                    }
                });
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
                    }
                });
    }

    private void runLottery(int numberOfWinners) {
        if (participantList.isEmpty()) {
            Toast.makeText(getContext(), "Waitlist is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Participant> shuffledList = new ArrayList<>(participantList);
        Collections.shuffle(shuffledList);

        int actualWinners = Math.min(numberOfWinners, shuffledList.size());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
            Toast.makeText(getContext(), "Lottery complete!", Toast.LENGTH_SHORT).show();
            lotteryBtn.setEnabled(false);
            lotteryBtn.setText("Lottery Completed");
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lottery failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}