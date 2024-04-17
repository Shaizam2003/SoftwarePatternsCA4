package com.example.softwarepatternsca4.models;

import java.util.ArrayList;

public class Order {

    ArrayList<Order> productList;
    String userEmail;
    String date;
    String subtotal;

    public Order(ArrayList<Order> productList, String userEmail, String date, String subtotal) {
        this.productList = productList;
        this.userEmail = userEmail;
        this.date = date;
        this.subtotal = subtotal;
    }

    public Order() {
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "CustomOrder{" +
                "productList=" + productList +
                ", userEmail='" + userEmail + '\'' +
                ", date='" + date + '\'' +
                ", subtotal='" + subtotal + '\'' +
                '}';
    }

    public ArrayList<Order> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Order> productList) {
        this.productList = productList;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}
