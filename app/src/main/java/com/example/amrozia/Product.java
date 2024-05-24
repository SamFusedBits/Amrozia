package com.example.amrozia;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private List<String> picUrl;
    private float rating;
    private int numberInCart;
    // Default constructor is needed for Firebase
    public Product() {}

    public Product(String name, String description, double price, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.picUrl = picUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getNumberInCart() {
        return numberInCart;
    }
    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }
}