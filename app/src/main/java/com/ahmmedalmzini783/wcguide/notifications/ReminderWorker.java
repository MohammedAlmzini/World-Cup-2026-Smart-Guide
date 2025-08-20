package com.ahmmedalmzini783.wcguide.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.main.MainActivity;

public class ReminderWorker extends Worker {

    private static final String CHANNEL_ID = "event_reminders";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get reminder data from input
        String eventId = getInputData().getString("event_id");
        String eventTitle = getInputData().getString("event_title");
        String eventTime = getInputData().getString("event_time");
        String reminderTime = getInputData().getString("reminder_time");

        if (eventId != null && eventTitle != null) {
            showReminderNotification(eventId, eventTitle, eventTime, reminderTime);
            return Result.success();
        }

        return Result.failure();
    }

    private void showReminderNotification(String eventId, String eventTitle, String eventTime, String reminderTime) {
        createNotificationChannel();

        String title = getApplicationContext().getString(R.string.notification_reminder_title);
        String body = getApplicationContext().getString(R.string.notification_reminder_text, eventTitle, reminderTime);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("deep_link", "wcguide://event/" + eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                eventId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_events,
                        getApplicationContext().getString(R.string.event_details_title),
                        pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(eventId.hashCode(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.notification_channel_reminders);
            String description = "Event reminder notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager =
                    getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}