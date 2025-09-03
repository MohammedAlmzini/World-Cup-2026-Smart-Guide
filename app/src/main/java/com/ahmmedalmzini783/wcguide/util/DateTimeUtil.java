package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import com.ahmmedalmzini783.wcguide.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    public static final long WORLD_CUP_START_TIME = 1761325200000L; // June 11, 2026 (placeholder)

    /**
     * Format timestamp to readable date string
     */
    public static String formatDate(long timestamp, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * Format timestamp to readable time string
     */
    public static String formatTime(long timestamp, Locale locale) {
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        return timeFormat.format(new Date(timestamp));
    }

    /**
     * Format timestamp to readable date and time string
     */
    public static String formatDateTime(long timestamp, Locale locale) {
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        return dateTimeFormat.format(new Date(timestamp));
    }

    /**
     * Get relative time string (e.g., "2 hours ago", "in 3 days")
     */
    public static String getRelativeTime(Context context, long timestamp) {
        long now = System.currentTimeMillis();
        long diff = timestamp - now;

        if (Math.abs(diff) < TimeUnit.MINUTES.toMillis(1)) {
            return context.getString(R.string.time_now);
        }

        boolean future = diff > 0;
        diff = Math.abs(diff);

        if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return future ?
                    context.getString(R.string.time_in_minutes, minutes) :
                    context.getString(R.string.time_minutes_ago, minutes);
        }

        if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return future ?
                    context.getString(R.string.time_in_hours, hours) :
                    context.getString(R.string.time_hours_ago, hours);
        }

        if (diff < TimeUnit.DAYS.toMillis(7)) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return future ?
                    context.getString(R.string.time_in_days, days) :
                    context.getString(R.string.time_days_ago, days);
        }

        // For longer periods, show the actual date
        return formatDate(timestamp, Locale.getDefault());
    }

    /**
     * Get countdown to World Cup 2026
     */
    public static String getWorldCupCountdown(Context context) {
        long now = System.currentTimeMillis();
        long diff = WORLD_CUP_START_TIME - now;

        if (diff <= 0) {
            return context.getString(R.string.world_cup_started);
        }

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;

        return context.getString(R.string.countdown_format_with_seconds, days, hours, minutes, seconds);
    }

    /**
     * Check if event is happening now
     */
    public static boolean isEventLive(long startTime, long endTime) {
        long now = System.currentTimeMillis();
        return now >= startTime && now <= endTime;
    }

    /**
     * Check if event is upcoming (starts within next 24 hours)
     */
    public static boolean isEventUpcoming(long startTime) {
        long now = System.currentTimeMillis();
        long diff = startTime - now;
        return diff > 0 && diff <= TimeUnit.DAYS.toMillis(1);
    }

    /**
     * Get event status string
     */
    public static String getEventStatus(Context context, long startTime, long endTime) {
        long now = System.currentTimeMillis();

        if (now < startTime) {
            // Future event
            if (isEventUpcoming(startTime)) {
                return context.getString(R.string.event_status_upcoming);
            } else {
                return getRelativeTime(context, startTime);
            }
        } else if (now >= startTime && now <= endTime) {
            // Live event
            return context.getString(R.string.event_status_live);
        } else {
            // Past event
            return context.getString(R.string.event_status_ended);
        }
    }

    /**
     * Convert UTC timestamp to local timezone
     */
    public static long convertUtcToLocal(long utcTimestamp) {
        TimeZone localTimeZone = TimeZone.getDefault();
        return utcTimestamp + localTimeZone.getOffset(utcTimestamp);
    }

    /**
     * Get start of day timestamp
     */
    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Get end of day timestamp
     */
    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Format duration in minutes to readable string
     */
    public static String formatDuration(Context context, int durationMinutes) {
        if (durationMinutes < 60) {
            return context.getString(R.string.duration_minutes, durationMinutes);
        } else {
            int hours = durationMinutes / 60;
            int minutes = durationMinutes % 60;
            if (minutes == 0) {
                return context.getString(R.string.duration_hours, hours);
            } else {
                return context.getString(R.string.duration_hours_minutes, hours, minutes);
            }
        }
    }
}