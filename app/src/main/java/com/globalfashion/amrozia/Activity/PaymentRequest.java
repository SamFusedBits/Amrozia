package com.globalfashion.amrozia.Activity;

// The PaymentRequest class represents the request object for creating a payment order
public class PaymentRequest {
    private String orderAmount;
    private String orderCurrency;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Constructor for the PaymentRequest class
    public PaymentRequest(String orderAmount, String orderCurrency, String customerName, String customerPhone, String customerEmail) {
        this.orderAmount = orderAmount;
        this.orderCurrency = orderCurrency;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
    }

    // Getters and Setters (optional if not using any serialization/deserialization library that requires them)
    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}

