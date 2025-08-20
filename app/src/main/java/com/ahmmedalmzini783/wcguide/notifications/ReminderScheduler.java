package com.ahmmedalmzini783.wcguide.notifications;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.util.DateTimeUtil;

import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    private final Context context;
    private final WorkManager workManager;

    public ReminderScheduler(Context context) {
        this.context = context;
        this.workManager = WorkManager.getInstance(context);
    }

    public void scheduleEventReminder(Event event, long reminderTimeMillis) {
        long currentTime = System.currentTimeMillis();
        long delay = reminderTimeMillis - currentTime;

        if (delay <= 0) {
            // Reminder time has already passed
            return;
        }

        String reminderTimeText = DateTimeUtil.getRelativeTime(context, reminderTimeMillis);

        Data inputData = new Data.Builder()
                .putString("event_id", event.getId())
                .putString("event_title", event.getTitle())
                .putString("event_time", DateTimeUtil.formatDateTime(event.getStartUtc(), context.getResources().getConfiguration().getLocales().get(0)))
                .putString("reminder_time", reminderTimeText)
                .build();

        OneTimeWorkRequest reminderWork = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("event_reminder_" + event.getId())
                .build();

        workManager.enqueue(reminderWork);
    }

    public void scheduleEventReminder(Event event, ReminderType reminderType) {
        long reminderTime = calculateReminderTime(event.getStartUtc(), reminderType);
        scheduleEventReminder(event, reminderTime);
    }

    public void cancelEventReminder(String eventId) {
        workManager.cancelAllWorkByTag("event_reminder_" + eventId);
    }

    public void cancelAllReminders() {
        workManager.cancelAllWorkByTag("event_reminder");
    }

    private long calculateReminderTime(long eventStartTime, ReminderType reminderType) {
        switch (reminderType) {
            case FIFTEEN_MINUTES:
                return eventStartTime - TimeUnit.MINUTES.toMillis(15);
            case ONE_HOUR:
                return eventStartTime - TimeUnit.HOURS.toMillis(1);
            case ONE_DAY:
                return eventStartTime - TimeUnit.DAYS.toMillis(1);
            case ONE_WEEK:
                return eventStartTime - TimeUnit.DAYS.toMillis(7);
            default:
                return eventStartTime - TimeUnit.HOURS.toMillis(1);
        }
    }

    public enum ReminderType {
        FIFTEEN_MINUTES,
        ONE_HOUR,
        ONE_DAY,
        ONE_WEEK
    }
}