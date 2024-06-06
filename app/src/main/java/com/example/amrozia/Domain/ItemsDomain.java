package com.example.amrozia.Domain;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemsDomain implements Serializable {
    private String id;
    private String title;
    private String description;
    private ArrayList<String> picUrl;
    private double price;
    private String category;
    private int NumberinCart;
    private String size;

    private int quantity;

    // Default constructor is needed for Firebase
    public ItemsDomain() {
    }

    public ItemsDomain(String id, String title, String description, ArrayList<String> picUrl, double price, String category, String size) {
        this.id=id;
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.price = price;
        this.category = category;
        this.size = size;
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

    public int getNumberinCart() {
        return NumberinCart;
    }

    public void setNumberinCart(int numberinCart) {
        this.NumberinCart = numberinCart;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}