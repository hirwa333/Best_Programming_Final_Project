package com.impano.logistics.model;

public class Driver extends User {
    private String licenseNumber;
    private boolean available;

    public Driver(String userId, String name, String email, String password, String licenseNumber) {
        super(userId, name, email, password, Role.DRIVER);
        this.licenseNumber = licenseNumber;
        this.available = true;
    }

    public String getLicenseNumber() { return licenseNumber; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("Driver{id=%s, name=%s, license=%s, available=%s}",
                getUserId(), getName(), licenseNumber, available);
    }
}
