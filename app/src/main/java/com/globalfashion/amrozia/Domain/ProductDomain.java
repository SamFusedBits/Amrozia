package com.globalfashion.amrozia.Domain;

import java.io.Serializable;
import java.util.ArrayList;
// This class is used to store the product details
public class ProductDomain implements Serializable {
    // Fields for the product details
    private String id;
    private String title;
    private String description;
    private double price;
    private ArrayList<String> picUrl;
    private String category;
    private int stock;
    private int quantity;
    private String name;
    // Default constructor is needed for Firebase
    public ProductDomain() {}

    public ProductDomain(String id, String title, String description, double price, ArrayList<String> picUrl, String category, int stock, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.picUrl = picUrl;
        this.category = category;
        this.stock = stock;
        this.quantity = quantity;
        this.name = name;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}