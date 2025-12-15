package com.MediConnect.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "queue_entries")
@CompoundIndexes({
        @CompoundIndex(name = "dispensary_status_idx", def = "{'dispensary': 1, 'status': 1, 'position': 1}"),
        @CompoundIndex(name = "doctor_status_idx", def = "{'doctor': 1, 'status': 1, 'position': 1}"),
        @CompoundIndex(name = "patient_created_idx", def = "{'patient': 1, 'createdAt': -1}")
})
public class QueueEntry {
    @Id
    private String id;

    @DBRef
    @Indexed
    private Patient patient;

    @DBRef
    @Indexed
    private Doctor doctor;

    @DBRef
    @Indexed
    private Dispensary dispensary;

    // Queue Information
    private Integer queueNumber; // Daily queue number (resets daily)
    private Integer position; // Current position in queue

    @Indexed
    private QueueStatus status = QueueStatus.WAITING;

    // Patient Information
    private String chiefComplaint; // Main reason for visit
    private String symptoms;
    private String notes;
    private Priority priority = Priority.NORMAL;

    // Timestamps
    private LocalDateTime joinedAt;
    private LocalDateTime calledAt;
    private LocalDateTime consultationStartedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    // Time Estimates
    private Integer estimatedWaitTime; // in minutes
    private LocalDateTime estimatedCallTime;

    // Notifications
    private boolean notificationSent = false;
    private LocalDateTime notificationSentAt;
    private boolean reminderSent = false;

    // Check-in status
    private boolean checkedIn = false;
    private LocalDateTime checkInTime;

    // Cancellation/No-show information
    private String cancellationReason;
    private String cancelledBy; // Patient, Doctor, System

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum QueueStatus {
        WAITING("Waiting in queue"),
        CALLED("Called for consultation"),
        IN_CONSULTATION("Currently with doctor"),
        COMPLETED("Consultation completed"),
        CANCELLED("Cancelled by patient/staff"),
        NO_SHOW("Patient did not show up"),
        POSTPONED("Postponed to later time");

        private final String description;

        QueueStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Priority {
        EMERGENCY(1, "Emergency"),
        HIGH(2, "High Priority"),
        NORMAL(3, "Normal"),
        LOW(4, "Low Priority");

        private final int level;
        private final String description;

        Priority(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }
    }

    // Helper methods
    public boolean isActive() {
        return status == QueueStatus.WAITING ||
                status == QueueStatus.CALLED ||
                status == QueueStatus.IN_CONSULTATION;
    }

    public boolean isCompleted() {
        return status == QueueStatus.COMPLETED ||
                status == QueueStatus.CANCELLED ||
                status == QueueStatus.NO_SHOW;
    }

    public long getWaitingTimeInMinutes() {
        if (joinedAt == null) return 0;
        LocalDateTime endTime = calledAt != null ? calledAt : LocalDateTime.now();
        return Duration.between(joinedAt, endTime).toMinutes();
    }

    public long getConsultationTimeInMinutes() {
        if (consultationStartedAt == null || completedAt == null) return 0;
        return Duration.between(consultationStartedAt, completedAt).toMinutes();
    }

    public long getTotalTimeInMinutes() {
        if (joinedAt == null) return 0;
        LocalDateTime endTime = completedAt != null ? completedAt : LocalDateTime.now();
        return Duration.between(joinedAt, endTime).toMinutes();
    }

    public boolean shouldSendNotification() {
        return !notificationSent && position != null && position <= 3;
    }

    public void markAsNotified() {
        this.notificationSent = true;
        this.notificationSentAt = LocalDateTime.now();
    }

    public void updatePosition(int newPosition) {
        this.position = newPosition;
        // Recalculate estimated wait time
        if (doctor != null && doctor.getAverageConsultationTime() != null) {
            this.estimatedWaitTime = (newPosition - 1) * doctor.getAverageConsultationTime();
            this.estimatedCallTime = LocalDateTime.now().plusMinutes(this.estimatedWaitTime);
        }
    }

    public void callPatient() {
        this.status = QueueStatus.CALLED;
        this.calledAt = LocalDateTime.now();
    }

    public void startConsultation() {
        this.status = QueueStatus.IN_CONSULTATION;
        this.consultationStartedAt = LocalDateTime.now();
    }

    public void completeConsultation() {
        this.status = QueueStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel(String reason, String cancelledBy) {
        this.status = QueueStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;
    }

    public void markAsNoShow() {
        this.status = QueueStatus.NO_SHOW;
        this.completedAt = LocalDateTime.now();
    }
}