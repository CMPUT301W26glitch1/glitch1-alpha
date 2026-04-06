package com.example.eventlotterysystemapp.data.models;

/**
 * Participant class used to store a user as a participant to an event
 * It is linked to a corresponding user account using email
 */
public class Participant {
    private String email;
    private String status;

    public Participant() {} // Firestore requirement
    public Participant(String email, String status) {
        this.email = email;
        this.status = status;
    }
    public String getEmail() { return email; }
    public String getStatus() { return status; }

    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
}