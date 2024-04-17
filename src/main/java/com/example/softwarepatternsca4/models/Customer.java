package com.example.softwarepatternsca4.models;

public class Customer {
    String customerName, customerAddress, customerPaymentMethod, customerEmail;
    Boolean isCustomerAdmin;

    public Customer(String customerName, String customerEmail, String customerAddress, String customerPaymentMethod, Boolean isCustomerAdmin) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerPaymentMethod = customerPaymentMethod;
        this.isCustomerAdmin = isCustomerAdmin;
    }

    public Customer() {
        // Required empty public constructor
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPaymentMethod() {
        return customerPaymentMethod;
    }

    public void setCustomerPaymentMethod(String customerPaymentMethod) {
        this.customerPaymentMethod = customerPaymentMethod;
    }

    public Boolean getCustomerAdmin() {
        return isCustomerAdmin;
    }

    public void setCustomerAdmin(Boolean customerAdmin) {
        isCustomerAdmin = customerAdmin;
    }
}
