package com.example.walkinclinic;

public class Patient extends User {
    public Patient() {}

    public Patient(String username, String password,String id) {
        super(username, password,id);
        this.type = MainActivity.PATIENT;
    }
}
