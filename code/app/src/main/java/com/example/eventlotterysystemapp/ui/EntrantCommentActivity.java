package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.EntrantComment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class EntrantCommentActivity extends AppCompatActivity {
    private String eventId, userEmail;
    private FirebaseFirestore db;
    private ArrayList<EntrantComment> commentList = new ArrayList<>();
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_comment);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("EVENT_ID");
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        // --- 1. THE BACK BUTTON FIX ---
        ImageButton btnBack = findViewById(R.id.btnBackToEvents);
        btnBack.setOnClickListener(v -> finish()); // This closes the screen

        // --- 2. SET UP THE LIST (VIEWING) ---
        RecyclerView rv = findViewById(R.id.rvComments);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(commentList);
        rv.setAdapter(adapter);

        // --- 3. POST COMMENT LOGIC ---
        EditText etComment = findViewById(R.id.etComment);
        Button btnPost = findViewById(R.id.btnPostComment);
        btnPost.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();
            if (!text.isEmpty() && eventId != null) {
                EntrantComment newComment = new EntrantComment(userEmail, text);
                db.collection("events").document(eventId)
                        .collection("comments").add(newComment)
                        .addOnSuccessListener(doc -> etComment.setText(""));
            }
        });

        // --- 4. LIVE UPDATES (SEEING YOUR POSTS) ---
        if (eventId != null) {
            db.collection("events").document(eventId)
                    .collection("comments")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            commentList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                commentList.add(doc.toObject(EntrantComment.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    // --- MINI ADAPTER (STAYS INSIDE THIS FILE) ---
    private class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
        private ArrayList<EntrantComment> list;
        public CommentAdapter(ArrayList<EntrantComment> list) { this.list = list; }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup p, int vt) {
            android.view.View v = android.view.LayoutInflater.from(p.getContext())
                    .inflate(android.R.layout.simple_list_item_2, p, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder h, int pos) {
            EntrantComment c = list.get(pos);
            h.t1.setText(c.getUserName());
            h.t2.setText(c.getText());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView t1, t2;
            public ViewHolder(android.view.View v) {
                super(v);
                t1 = v.findViewById(android.R.id.text1);
                t2 = v.findViewById(android.R.id.text2);
            }
        }
    }
}