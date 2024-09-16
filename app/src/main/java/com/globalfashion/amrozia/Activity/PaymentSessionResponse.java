package com.globalfashion.amrozia.Activity;

// The PaymentSessionResponse class is responsible for storing the response data from the payment session
public class PaymentSessionResponse {
    // Define the fields for the PaymentSessionResponse class to store the response data
    private String orderID, paymentSessionID, orderAmount, orderCurrency, customerName, customerPhone, customerEmail, status;

    public String getOrderID() {
        return orderID;
    }

    public String getPaymentSessionID() {
        return paymentSessionID;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}