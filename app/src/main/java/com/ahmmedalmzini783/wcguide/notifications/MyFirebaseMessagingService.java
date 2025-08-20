package com.ahmmedalmzini783.wcguide.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID_REMINDERS = "event_reminders";
    private static final String CHANNEL_ID_UPDATES = "app_updates";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload
        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotificationMessage(remoteMessage);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void handleDataMessage(Map<String, String> data) {
        String type = data.get("type");
        String eventId = data.get("eventId");
        String placeId = data.get("placeId");

        if ("event_reminder".equals(type) && eventId != null) {
            showEventReminderNotification(data);
        } else if ("app_update".equals(type)) {
            showAppUpdateNotification(data);
        } else if ("deep_link".equals(type)) {
            handleDeepLink(data);
        }
    }

    private void handleNotificationMessage(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        String title = notification.getTitle();
        String body = notification.getBody();
        String channelId = CHANNEL_ID_UPDATES;

        // Check data payload for channel selection
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("channel")) {
            channelId = data.get("channel");
        }

        showNotification(title, body, channelId, data);
    }

    private void showEventReminderNotification(Map<String, String> data) {
        String eventTitle = data.get("eventTitle");
        String eventTime = data.get("eventTime");
        String eventId = data.get("eventId");

        String title = getString(R.string.notification_reminder_title);
        String body = getString(R.string.notification_reminder_text, eventTitle, eventTime);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("deep_link", "wcguide://event/" + eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_REMINDERS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(eventId.hashCode(), notificationBuilder.build());
    }

    private void showAppUpdateNotification(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");

        showNotification(title, body, CHANNEL_ID_UPDATES, data);
    }

    private void showNotification(String title, String body, String channelId, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);

        // Handle deep links
        if (data != null && data.containsKey("deep_link")) {
            intent.putExtra("deep_link", data.get("deep_link"));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        if (body != null && body.length() > 50) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

    private void handleDeepLink(Map<String, String> data) {
        String deepLink = data.get("deep_link");
        if (deepLink != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("deep_link", deepLink);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            // Event Reminders Channel
            NotificationChannel remindersChannel = new NotificationChannel(
                    CHANNEL_ID_REMINDERS,
                    getString(R.string.notification_channel_reminders),
                    NotificationManager.IMPORTANCE_HIGH
            );
            remindersChannel.setDescription("Notifications for World Cup event reminders");
            remindersChannel.enableVibration(true);
            remindersChannel.setShowBadge(true);

            // App Updates Channel
            NotificationChannel updatesChannel = new NotificationChannel(
                    CHANNEL_ID_UPDATES,
                    getString(R.string.notification_channel_updates),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            updatesChannel.setDescription("General app updates and news");
            updatesChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(remindersChannel);
            notificationManager.createNotificationChannel(updatesChannel);
        }
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Send token to your app server
        Log.d(TAG, "Sending token to server: " + token);

        // You can save this token to Firebase Realtime Database
        // or send it to your backend server for targeted messaging
    }
}