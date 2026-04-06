package com.example.eventlotterysystemapp.data.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
@IgnoreExtraProperties

/**
 * Event class to store information of an event
 */
public class Event {
    private String eventId;
    private String organizerId;
    private String name;
    private String description;
    private String category;
    private String location;
    private LocalDateTime dateTime;
    private LocalDateTime regStart;
    private LocalDateTime regEnd;
    private boolean geolocationReq;
    private String posterUrl;
    private int listLimit;
    private int maxParticipants;
    private boolean privateEvent;

    public Event() {}

    public Event(String name, String description, String category, String location,
                 LocalDateTime dateTime, LocalDateTime regStart, LocalDateTime regEnd,
                 boolean geolocationReq, String organizerId, String posterUrl, int listLimit, int maxParticipants, boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.location = location;
        this.dateTime = dateTime;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.geolocationReq = geolocationReq;
        this.organizerId = organizerId;
        this.posterUrl = posterUrl;
        this.listLimit = listLimit;
        this.maxParticipants = maxParticipants;
        this.privateEvent = isPrivate;
    }
    public boolean isPrivateEvent()
    {return privateEvent;}
    public void setPrivateEvent(boolean privateEvent) {this.privateEvent=privateEvent;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventId != null ? eventId.equals(event.eventId) : event.eventId == null;
    }

    @Override
    public int hashCode() {
        return eventId != null ? eventId.hashCode() : 0;
    }
    // --- Standard Getters/Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public boolean isGeolocationReq() { return geolocationReq; }
    public void setGeolocationReq(boolean geolocationReq) { this.geolocationReq = geolocationReq; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public int getListLimit() { return listLimit; }
    public void setListLimit(int listLimit) { this.listLimit = listLimit; }

    public boolean isPrivate() { return privateEvent; }
    public void setPrivate(boolean privateEvent) { this.privateEvent = privateEvent; }
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    // --- LocalDateTime Logic for Firestore ---

    @Exclude
    public LocalDateTime getDateTime() { return dateTime; }
    public void updateLocalDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }


    @PropertyName("id")
    public String getFirebaseId() {return eventId;}
    @PropertyName("id")
    public void setFirebaseId(String id) {this.eventId=id;}
    @PropertyName("dateTime")
    public Date getDateTimeAsDate() {
        return (dateTime == null) ? null : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @PropertyName("dateTime")
    public void setDateTime(Date date) {
        this.dateTime = (date == null) ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Exclude
    public LocalDateTime getRegStart() { return regStart; }
    public void updateLocalRegStart(LocalDateTime regStart) { this.regStart = regStart; }

    @PropertyName("regStart")
    public Date getRegStartAsDate() {
        return (regStart == null) ? null : Date.from(regStart.atZone(ZoneId.systemDefault()).toInstant());
    }

    @PropertyName("regStart")
    public void setRegStart(Date date) {
        this.regStart = (date == null) ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Exclude
    public LocalDateTime getRegEnd() { return regEnd; }
    public void updateLocalRegEnd(LocalDateTime regEnd) { this.regEnd = regEnd; }

    @PropertyName("regEnd")
    public Date getRegEndAsDate() {
        return (regEnd == null) ? null : Date.from(regEnd.atZone(ZoneId.systemDefault()).toInstant());
    }

    @PropertyName("regEnd")
    public void setRegEnd(Date date) {
        this.regEnd = (date == null) ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}