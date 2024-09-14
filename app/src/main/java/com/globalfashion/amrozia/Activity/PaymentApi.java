package com.globalfashion.amrozia.Activity;

import static com.globalfashion.amrozia.BuildConfig.API_VERSION;
import static com.globalfashion.amrozia.BuildConfig.CLIENT_ID;
import static com.globalfashion.amrozia.BuildConfig.CLIENT_SECRET;
import static com.globalfashion.amrozia.BuildConfig.CONTENT_TYPE;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
// The PaymentApi interface is responsible for creating a POST request to the /orders endpoint to create an order with the given PaymentRequest object
public interface PaymentApi {
    // Create a POST request to the /orders endpoint to create an order with the given PaymentRequest object
    @Headers({
            "Content-Type: " + CONTENT_TYPE,
            "x-client-id: " + CLIENT_ID,
            "x-client-secret: " + CLIENT_SECRET,
            "x-api-version: " + API_VERSION
    })
    @POST("orders")
    Call<PaymentSessionResponse> createOrder(@Body PaymentRequest paymentRequest);
}
