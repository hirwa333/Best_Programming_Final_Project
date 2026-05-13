package com.impano.logistics.model;

public class Admin extends User {

    public Admin(String userId, String name, String email, String password) {
        super(userId, name, email, password, Role.ADMIN);
    }

    @Override
    public String toString() {
        return String.format("Admin{id=%s, name=%s, email=%s}", getUserId(), getName(), getEmail());
    }
}
