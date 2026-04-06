package com.example.eventlotterysystemapp.data.models;

import com.google.firebase.Timestamp;

public class EntrantComment {
    private String userName;
    private String text;
    private Timestamp timestamp;

    public EntrantComment() {}

    public EntrantComment(String userName, String text, Timestamp timestamp) {
        this.userName = userName;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}