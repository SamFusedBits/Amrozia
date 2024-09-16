package com.globalfashion.amrozia;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
// This service class is used to receive and display Firebase Cloud Messaging notifications
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    // Constants for notification channel and ID
    private static final String CHANNEL_ID = "MyNotificationChannel";
    // Notification ID for the app and permission code
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            // Get the notification title and message
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            // Display the notification
            showNotification(title, message);
        }
    }

    // Display a notification with the given title and message
    private void showNotification(String title, String message) {
        // Create a notification channel
        createNotificationChannel();

        // Create an intent that starts the main activity
        Intent intent = new Intent(this, MainActivity.class);
        // Set the intent flags to clear the task and start a new task
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create a pending intent to open the main activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the notification priority to high and make it auto-cancel
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon_image)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        // Get the notification manager and display the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the app has the POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Request permission if it is not granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // For Android 13 and above, we can't request permission from a service
                // Instead, create a notification to prompt the user to open the app and grant permission
                createPermissionPromptNotification();
                return;
            }
        }
        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // Create a notification channel for the app
    private void createNotificationChannel() {
        // Check if the Android version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel with the given ID, name, and importance
            CharSequence name = "My Notification Channel";
            String description = "Channel for my app notifications";
            // Set the importance to high
            int importance = NotificationManager.IMPORTANCE_HIGH;
            // Create the notification channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Set the description for the channel
            channel.setDescription(description);

            // Get the notification manager and create the channel
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Create a notification to prompt the user to open the app and grant permission
    private void createPermissionPromptNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon_image)
                .setContentTitle("Permission Required")
                .setContentText("Please open the app to enable notifications")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Get the notification manager and display the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Display the notification with a different ID
        notificationManager.notify(NOTIFICATION_ID + 1, builder.build());
    }

    // Method to handle token refresh and send the token to the server
    @Override
    public void onNewToken(@NonNull String token) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Implement this method to send token to your app server.
    }
}
