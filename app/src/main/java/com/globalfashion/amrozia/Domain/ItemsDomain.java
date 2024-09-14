package com.globalfashion.amrozia.Domain;

import java.io.Serializable;
import java.util.ArrayList;
// The ItemsDomain class contains the detail for the product displayed in the app
public class ItemsDomain implements Serializable {
    private String id;
    private String title;
    private String description;
    private ArrayList<String> picUrl;
    private double price;
    private String category;
    private int quantity;

    // Default constructor is needed for Firebase
    public ItemsDomain() {
    }

    // Constructor to initialize the fields
    public ItemsDomain(String id, String title, String description, ArrayList<String> picUrl, double price, String category) {
        this.id=id;
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.price = price;
        this.category = category;
        this.quantity = 1; // Default quantity is 1
    }

    // Getters and setters for all fields
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}