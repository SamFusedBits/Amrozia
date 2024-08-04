package com.example.amrozia.Domain;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductDomain implements Serializable {

    private String id;
    private String title;
    private String description;
    private double price;
    private ArrayList<String> picUrl;
    private String category;

    // Default constructor is needed for Firebase
    public ProductDomain() {}

    public ProductDomain(String id, String title, String description, double price, ArrayList<String> picUrl, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.picUrl = picUrl;
        this.category = category;
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
}