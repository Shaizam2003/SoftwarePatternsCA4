package com.example.softwarepatternsca4.signin;

public class User {
    private String name;
    private String email;
    private String address;
    private String paymentMethod;
    private boolean isAdmin;

    // Constructor
    public User(String name, String email, String address, String paymentMethod, boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.isAdmin = isAdmin;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
