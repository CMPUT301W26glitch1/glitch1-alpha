package com.example.eventlotterysystemapp.data.models;
/**
 * User Session class to know which user is currently logged in
 */
public class UserSession {

    private static User currentUser;

    public static void setUser(User user){
        currentUser = user;
    }

    public static User getUser(){
        return currentUser;
    }
}