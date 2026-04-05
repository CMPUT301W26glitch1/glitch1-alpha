package com.example.eventlotterysystemapp.data.models;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String eventId;
    private String recipientId;
    private String recipientEmail;
    private String message;
    private String type; // "LOTTERY_WIN", "LOTTERY_LOSS", "PRIVATE_INVITE", "BROADCAST"
    private String status; // "pending", "accepted", "declined"
    private com.google.firebase.Timestamp timestamp;

    public Notification() {}

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}