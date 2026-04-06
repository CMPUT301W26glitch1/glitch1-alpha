package com.example.eventlotterysystemapp.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class EntrantComment {
    private String userName;
    private String text;
    @ServerTimestamp
    private Timestamp timestamp;

    public EntrantComment() {} // Needed for Firebase

    public EntrantComment(String userName, String text) {
        this.userName = userName;
        this.text = text;
    }

    public String getUserName() { return userName; }
    public String getText() { return text; }
    public Timestamp getTimestamp() { return timestamp; }
}