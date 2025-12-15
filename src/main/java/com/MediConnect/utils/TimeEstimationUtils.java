// TimeEstimationUtils.java
package com.MediConnect.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeEstimationUtils {

    /**
     * Format duration in minutes to human-readable string
     */
    public static String formatWaitTime(int minutes) {
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            if (mins == 0) {
                return hours + (hours == 1 ? " hour" : " hours");
            }
            return hours + (hours == 1 ? " hour " : " hours ") + mins + " minutes";
        }
    }

    /**
     * Calculate time difference in minutes
     */
    public static long getMinutesBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMinutes();
    }

    /**
     * Check if current time is within working hours
     */
    public static boolean isWithinWorkingHours(LocalTime currentTime, LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null) {
            return true; // Assume 24/7 if not specified
        }
        return !currentTime.isBefore(openTime) && !currentTime.isAfter(closeTime);
    }

    /**
     * Get time until opening
     */
    public static long getMinutesUntilOpen(LocalTime currentTime, LocalTime openTime) {
        if (openTime == null) {
            return 0;
        }
        if (currentTime.isBefore(openTime)) {
            return Duration.between(currentTime, openTime).toMinutes();
        }
        // If past opening time, calculate time until next day's opening
        return Duration.between(currentTime, LocalTime.MAX).toMinutes() +
                Duration.between(LocalTime.MIN, openTime).toMinutes();
    }

    /**
     * Estimate closing time based on queue length
     */
    public static LocalDateTime estimateClosingTime(int queueLength, int avgConsultationTime, LocalDateTime currentTime) {
        int totalMinutes = queueLength * avgConsultationTime;
        return currentTime.plusMinutes(totalMinutes);
    }
}