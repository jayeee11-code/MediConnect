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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;

    @DBRef
    private User user;

    // Professional Information
    private String qualification; // MBBS, MD, etc.
    private String specialization; // General Physician, Cardiologist, etc.

    @Indexed(unique = true)
    private String licenseNumber;

    private Integer yearsOfExperience;
    private List<String> expertise = new ArrayList<>();
    private List<String> languages = new ArrayList<>();

    // Profile
    private String profileImageUrl;
    private String bio;
    private String education;

    // Availability
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.NOT_AVAILABLE;
    private LocalDateTime statusLastUpdated;

    // Consultation Details
    private Double consultationFee;
    private Integer averageConsultationTime = 15; // in minutes

    // Statistics
    private Integer totalConsultations = 0;
    private Double rating = 0.0;
    private Integer totalReviews = 0;

    // Working Schedule
    private WorkingSchedule workingSchedule;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum AvailabilityStatus {
        AVAILABLE("Available for consultation"),
        ON_BREAK("Taking a break"),
        NOT_AVAILABLE("Not available"),
        BUSY("Currently with patient");

        private final String description;

        AvailabilityStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingSchedule {
        private List<WorkingDay> workingDays = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingDay {
        private String day; // MONDAY, TUESDAY, etc.
        private String startTime; // HH:MM format
        private String endTime; // HH:MM format
        private boolean isWorking;
    }

    // Helper methods
    public boolean isAvailable() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE;
    }

    public void incrementConsultations() {
        this.totalConsultations++;
    }

    public void updateRating(double newRating) {
        double totalRatingPoints = this.rating * this.totalReviews;
        totalRatingPoints += newRating;
        this.totalReviews++;
        this.rating = Math.round((totalRatingPoints / this.totalReviews) * 10.0) / 10.0;
    }
}
