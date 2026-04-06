package com.example.eventlotterysystemapp.data.models;

import com.google.firebase.Timestamp; // Added import

public class Comment {
    private String commentId;
    private String text;
    private String userId;
    private String userName;
    private Timestamp timestamp;

    public Comment() {
    }

    public Comment(String commentId, String text, String userId, String userName, Timestamp timestamp) {
        this.commentId = commentId;
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}