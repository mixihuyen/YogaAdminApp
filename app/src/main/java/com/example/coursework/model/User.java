package com.example.coursework.model;

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public User() {
        // Firestore yêu cầu một constructor rỗng
    }

    public User(String email, String firstName, String lastName, String phoneNumber) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
}
