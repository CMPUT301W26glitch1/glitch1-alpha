package com.example.eventlotterysystemapp.data.models;

import java.sql.Time;
import java.util.Date;

enum Category{
    MUSIC,
    SCIENCE,
    NATURE,
    ARTS
}

public class Event {
    private String name;
    private String description;
    private Category category;
    private String location;
    private Date date;
    private Time time;
    private Date regStartDate;
    private Time regStartTime;
    private Date regEndDate;
    private Time regEndTime;
    private boolean geolocation;

    public Event(Time regEndTime, Date regEndDate, Time regStartTime, Date regStartDate, Time time, Date date, String location, Category category, String description, String name, boolean geolocation) {
        this.regEndTime = regEndTime;
        this.regEndDate = regEndDate;
        this.regStartTime = regStartTime;
        this.regStartDate = regStartDate;
        this.time = time;
        this.date = date;
        this.location = location;
        this.category = category;
        this.description = description;
        this.name = name;
        this.geolocation = geolocation;
    }


    // -----Getters and Setters-----

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Date getRegStartDate() {
        return regStartDate;
    }

    public void setRegStartDate(Date regStartDate) {
        this.regStartDate = regStartDate;
    }

    public Time getRegStartTime() {
        return regStartTime;
    }

    public void setRegStartTime(Time regStartTime) {
        this.regStartTime = regStartTime;
    }

    public Date getRegEndDate() {
        return regEndDate;
    }

    public void setRegEndDate(Date regEndDate) {
        this.regEndDate = regEndDate;
    }

    public Time getRegEndTime() {
        return regEndTime;
    }

    public void setRegEndTime(Time regEndTime) {
        this.regEndTime = regEndTime;
    }

    public boolean isGeolocation() {
        return geolocation;
    }

    public void setGeolocation(boolean geolocation) {
        this.geolocation = geolocation;
    }
}
