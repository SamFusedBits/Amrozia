package com.example.amrozia.Adapter;

import com.google.firebase.Timestamp;
import java.util.List;

public class Order {
    private String address;
    private List<String> category;
    private String email;
    private String name;
    private String phone;
    private List<String> productId;
    private List<String> productImages;
    private List<String> productNames;
    private List<String> productPrices;
    private List<Integer> productQuantities;
    private Timestamp timestamp;
    private String userId;
    private String trackingLink;

    private String orderId;

    // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    public Order() {}

    // Getters and Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getProductId() {
        return productId;
    }

    public void setProductId(List<String> productId) {
        this.productId = productId;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }

    public List<String> getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(List<String> productPrices) {
        this.productPrices = productPrices;
    }

    public List<Integer> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(List<Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
            return orderId;
    }

    public void setOrderId(String orderId) {
            this.orderId = orderId;
    }

    public String getTrackingLink() {
        return trackingLink;
    }

    public void setTrackingLink(String trackingLink) {
        this.trackingLink = trackingLink;
    }
}

