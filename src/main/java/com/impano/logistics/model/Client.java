package com.impano.logistics.model;

public class Client extends User {
    private String phone;
    private String address;

    public Client(String userId, String name, String email, String password, String phone, String address) {
        super(userId, name, email, password, Role.CLIENT);
        this.phone = phone;
        this.address = address;
    }

    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return String.format("Client{id=%s, name=%s, phone=%s, address=%s}", getUserId(), getName(), phone, address);
    }
}
