package com.example.eventlotterysystemapp.data.models;

/**
 * User class object used to store information of user accounts
 */
public class User {
    private String name;
    private String password;
    private String email;
    private String role;
    private String lastDeviceId;
    private String phoneNumber;
    private boolean notificationsOptedOut;


    public User(String name, String password, String email, String role, String lastDeviceId) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.lastDeviceId = lastDeviceId;
        this.phoneNumber = "-1";
    }

    // -----getters and setters-----

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber(){return phoneNumber.toString();}
    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }


    public String getLastDeviceId() {
        return lastDeviceId;
    }

    public void setLastDeviceId(String lastDeviceId) {
        this.lastDeviceId = lastDeviceId;
    }

    public boolean isNotificationsOptedOut() { return notificationsOptedOut; }

    public void setNotificationsOptedOut(boolean notificationsOptedOut) {
        this.notificationsOptedOut = notificationsOptedOut;
    }
    public User() {}
}
