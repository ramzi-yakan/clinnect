package com.example.walkinclinic;

public class Admin extends User {
    public Admin() {}

    public Admin(String username, String password, String id) {
        super(username, password,id);
        this.type = MainActivity.ADMIN;
    }
}
