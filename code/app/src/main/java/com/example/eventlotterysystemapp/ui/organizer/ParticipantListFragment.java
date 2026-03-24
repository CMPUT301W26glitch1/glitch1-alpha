package com.example.eventlotterysystemapp.ui.organizer;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Participant;
import com.example.eventlotterysystemapp.data.models.ParticipantAdapter;
import com.example.eventlotterysystemapp.ui.organizer.NotificationActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragement for EventParticipantsActivity that lists the participants of an event
 */
public class ParticipantListFragment extends Fragment {
    private String eventId, status;
    private RecyclerView recyclerView;
    private ParticipantAdapter adapter;
    private List<Participant> participantList = new ArrayList<>();

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
        // Inflate your fragment layout
        return inflater.inflate(R.layout.fragment_participant_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventId = getArguments().getString("eId");
        status = getArguments().getString("stat");

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.participantRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ParticipantAdapter(participantList);
        recyclerView.setAdapter(adapter);

        // Logic for the Lottery Button
        Button btnLottery = view.findViewById(R.id.btnLottery);
        Button btnNotification = view.findViewById(R.id.btnNotify);

        if ("waitlist".equals(status)) {
            btnLottery.setVisibility(View.VISIBLE);
            btnNotification.setVisibility(View.GONE);
            btnLottery.setOnClickListener(v -> { /* Lottery logic here later */ });
        } else if ("selected".equals(status)){
            btnLottery.setVisibility(View.GONE);
            btnNotification.setVisibility(View.VISIBLE);
            btnNotification.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                startActivity(intent);
            });
        } else {
            btnLottery.setVisibility(View.GONE);
            btnNotification.setVisibility(View.GONE);
        }

        // Fetch participants in real-time
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId).collection("participants")
                .whereEqualTo("status", status)
                .addSnapshotListener((querySnap, e) -> {
                    if (e != null) return;

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
}