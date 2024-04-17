package com.example.softwarepatternsca4.models;

import java.util.ArrayList;

public class Product {

    private String customKey;
    private String customTitle;
    private String customManufacturer;
    private String customPrice;
    private String customSize;
    private String customCategory;
    private String customImage;
    private int customStock;

    public Product() {
        // Required empty public constructor
    }

    public Product(String customKey, String customTitle, String customManufacturer, String customPrice, String customSize, String customCategory, String customImage, int customStock) {
        this.customKey = customKey;
        this.customTitle = customTitle;
        this.customManufacturer = customManufacturer;
        this.customPrice = customPrice;
        this.customSize = customSize;
        this.customCategory = customCategory;
        this.customImage = customImage;
        this.customStock = customStock;
    }

    public Product(ArrayList<Product> cart, String userEmail, String currentDateTime, String valueOf) {
    }

    public String getCustomKey() {
        return customKey;
    }

    public void setCustomKey(String customKey) {
        this.customKey = customKey;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public String getCustomManufacturer() {
        return customManufacturer;
    }

    public void setCustomManufacturer(String customManufacturer) {
        this.customManufacturer = customManufacturer;
    }

    public String getCustomPrice() {
        return customPrice;
    }

    public void setCustomPrice(String customPrice) {
        this.customPrice = customPrice;
    }

    public String getCustomSize() {
        return customSize;
    }

    public void setCustomSize(String customSize) {
        this.customSize = customSize;
    }

    public String getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(String customCategory) {
        this.customCategory = customCategory;
    }

    public String getCustomImage() {
        return customImage;
    }

    public void setCustomImage(String customImage) {
        this.customImage = customImage;
    }

    public int getCustomStock() {
        return customStock;
    }

    public void setCustomStock(int customStock) {
        this.customStock = customStock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "customKey='" + customKey + '\'' +
                ", customTitle='" + customTitle + '\'' +
                ", customManufacturer='" + customManufacturer + '\'' +
                ", customPrice='" + customPrice + '\'' +
                ", customSize='" + customSize + '\'' +
                ", customCategory='" + customCategory + '\'' +
                ", customImage='" + customImage + '\'' +
                ", customStock=" + customStock +
                '}';
    }
}
