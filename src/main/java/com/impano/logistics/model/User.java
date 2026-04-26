package com.impano.logistics.model;

public abstract class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private Role role;

    public User(String userId, String name, String email, String password, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", role, name, email);
    }
}
