package com.globalfashion.amrozia.Helper;

import static com.globalfashion.amrozia.BuildConfig.API_KEY_SENDINBLUE;
import static com.globalfashion.amrozia.BuildConfig.BUSINESS_EMAIL;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
// This class is used to send emails using the Sendinblue API
public class SendinblueHelper {
    // API key for Sendinblue
    private static final String API_KEY = API_KEY_SENDINBLUE;
    // URL for sending emails
    private static final String SEND_EMAIL_URL = "https://api.sendinblue.com/v3/smtp/email";
    // OkHttpClient for making network requests
    private static final OkHttpClient client = new OkHttpClient();

    // Method to send an order notification email
    public static void sendOrderNotification(String to, String subject, String orderDetails, Context context, EmailCallback callback) {
        String content = "<html><body></h1><p>" + orderDetails + "</p></body></html>";

        try {
            // Create a JSON object with the email details
            JSONObject json = new JSONObject();
            json.put("sender", new JSONObject().put("name", "Amrozia").put("email", BUSINESS_EMAIL));
            json.put("to", new JSONArray().put(new JSONObject().put("email", to)));
            json.put("subject", subject);
            json.put("htmlContent", content);

            // Create a request body with the JSON object
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
            Request request = new Request.Builder()
                    // Set the URL, content type, and API key in the request
                    .url(SEND_EMAIL_URL)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("api-key", API_KEY)
                    .build();

            // Make an asynchronous network request to send the email
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Handle the failure and return the result to the callback
                    callback.onResult(false, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Get the response status and message
                    boolean success = response.isSuccessful();
                    String message = response.message();
                    // Return the result to the callback on the main thread
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(success, message);
                        }
                    });
                }
            });
        } catch (Exception e) {
            callback.onResult(false, e.getMessage());
        }
    }

    // Callback interface to handle the email sending result
    public interface EmailCallback {
        // Method to handle the email sending result (success or failure)
        void onResult(boolean success, String message);
    }
}
