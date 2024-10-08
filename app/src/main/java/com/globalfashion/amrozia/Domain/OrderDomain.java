package com.globalfashion.amrozia.Domain;

import java.sql.Timestamp;
import java.util.Date;
// The OrderDomain class contains the order details for the myorders activity
public class OrderDomain {
    private String productName;
    private String productPrice;
    private String productImage;
    private Date timestamp;
    private String productId;
    private String category;
    private String trackingLink;

    public OrderDomain() {
        // Default constructor required for calls to DataSnapshot.getValue(OrderDomain.class)
    }

    public OrderDomain(String productName, String productPrice, String productImage, Timestamp timestamp, String productId, String category, String trackingLink) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.timestamp = timestamp;
        this.productId = productId;
        this.category = category;
        this.trackingLink = trackingLink;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTrackingLink() {
        return trackingLink;
    }

    public void setTrackingLink(String trackingLink) {
        this.trackingLink = trackingLink;
    }
}