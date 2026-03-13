package com.example.eventlotterysystemapp.data.models;

public class UserSession {

    private static User currentUser;

    public static void setUser(User user){
        currentUser = user;
    }

    public static User getUser(){
        return currentUser;
    }
}