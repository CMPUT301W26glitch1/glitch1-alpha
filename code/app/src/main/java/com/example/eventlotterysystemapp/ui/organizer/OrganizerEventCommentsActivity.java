package com.example.eventlotterysystemapp.ui.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.Comment;
import com.example.eventlotterysystemapp.data.models.CommentAdapter;
import com.example.eventlotterysystemapp.ui.AccessibilityUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEventCommentsActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private EditText etCommentInput;
    private Button btnPostComment;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String eventId;

    private boolean isAdminView = false;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_comments);
        AccessibilityUtils.applyAccessibilityMode(this);

        rvComments = findViewById(R.id.rvComments);
        etCommentInput = findViewById(R.id.etCommentInput);
        btnPostComment = findViewById(R.id.btnPostComment);

        rvComments.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        eventId = getIntent().getStringExtra("eventId");
        isAdminView = getIntent().getBooleanExtra("isAdminView", false);
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (isAdminView) {
            etCommentInput.setVisibility(View.GONE);
            btnPostComment.setVisibility(View.GONE);
        }

        commentAdapter = new CommentAdapter(this, commentList, eventId);
        rvComments.setAdapter(commentAdapter);

        btnPostComment.setOnClickListener(v -> postComment());

        loadComments();
    }

    public void goBackToMyEvents(View view) {
        finish();
    }

    private void postComment() {
        String text = etCommentInput.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String commentId = db.collection("events")
                .document(eventId)
                .collection("comments")
                .document()
                .getId();

        String userId = (currentUserId != null && !currentUserId.isEmpty()) ? currentUserId : "unknown_user";
        String userName = (currentUserName != null && !currentUserName.isEmpty()) ? currentUserName : "Organizer";

        long timestamp = System.currentTimeMillis();

        Comment comment = new Comment(commentId, text, userId, userName, timestamp);

        db.collection("events")
                .document(eventId)
                .collection("comments")
                .document(commentId)
                .set(comment)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
                    etCommentInput.setText("");
                    loadComments();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to post comment: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void loadComments() {
        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .collection("comments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    commentList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Comment comment = doc.toObject(Comment.class);
                        comment.setCommentId(doc.getId());
                        commentList.add(comment);
                    }

                    commentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load comments: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}