// QueueCalculator.java
package com.MediConnect.utils;

import com.MediConnect.models.Doctor;
import com.MediConnect.models.QueueEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class QueueCalculator {

    private static final int DEFAULT_CONSULTATION_TIME = 15; // minutes

    /**
     * Calculate estimated wait time for a patient based on their position in queue
     */
    public int calculateEstimatedWaitTime(int position, Integer avgConsultationTime) {
        int consultationTime = avgConsultationTime != null ? avgConsultationTime : DEFAULT_CONSULTATION_TIME;
        return (position - 1) * consultationTime;
    }

    /**
     * Calculate average consultation time for a doctor based on historical data
     */
    public int calculateAverageConsultationTime(List<QueueEntry> completedEntries) {
        if (completedEntries == null || completedEntries.isEmpty()) {
            return DEFAULT_CONSULTATION_TIME;
        }

        long totalMinutes = completedEntries.stream()
                .filter(entry -> entry.getCalledAt() != null && entry.getCompletedAt() != null)
                .mapToLong(entry -> ChronoUnit.MINUTES.between(
                        entry.getCalledAt(),
                        entry.getCompletedAt()
                ))
                .sum();

        long count = completedEntries.stream()
                .filter(entry -> entry.getCalledAt() != null && entry.getCompletedAt() != null)
                .count();

        return count > 0 ? (int) (totalMinutes / count) : DEFAULT_CONSULTATION_TIME;
    }

    /**
     * Calculate next queue number for a dispensary
     */
    public int calculateNextQueueNumber(int currentMaxNumber) {
        return currentMaxNumber + 1;
    }

    /**
     * Estimate time when patient will be called
     */
    public LocalDateTime estimateCallTime(int position, Integer avgConsultationTime) {
        int waitMinutes = calculateEstimatedWaitTime(position, avgConsultationTime);
        return LocalDateTime.now().plusMinutes(waitMinutes);
    }

    /**
     * Check if patient should receive notification (when their turn is approaching)
     */
    public boolean shouldNotifyPatient(QueueEntry entry, int notificationThreshold) {
        if (entry.getPosition() == null || entry.getPosition() > notificationThreshold) {
            return false;
        }
        return !entry.isNotificationSent();
    }
}

