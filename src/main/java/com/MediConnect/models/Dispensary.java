package com.MediConnect.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dispensaries")
public class Dispensary {
    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String licenseNumber;

    private String registrationNumber;

    @DBRef
    private User adminUser;

    @DBRef
    private List<Doctor> doctors = new ArrayList<>();

    // Location Information
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String phoneNumber;
    private String email;
    private String website;

    // Geolocation - MongoDB uses [longitude, latitude] order for GeoJSON
    @GeoSpatialIndexed
    private double[] location; // [longitude, latitude]

    // Images
    private String[] images;
    private String logoUrl;

    // Operating Hours
    private WorkingHours workingHours;

    // Services and Facilities
    private List<String> services = new ArrayList<>();
    private List<String> facilities = new ArrayList<>();
    private List<String> specializations = new ArrayList<>();

    // Status
    private boolean isOpen = false;
    private boolean isVerified = false;
    private boolean acceptsEmergency = false;

    // Queue Information
    private Integer currentQueueLength = 0;
    private Integer maxQueueCapacity = 50;

    // Ratings and Reviews
    private Double rating = 0.0;
    private Integer totalReviews = 0;

    // Additional Information
    private String description;
    private String parkingInfo;
    private boolean wheelchairAccessible = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingHours {
        private DaySchedule monday;
        private DaySchedule tuesday;
        private DaySchedule wednesday;
        private DaySchedule thursday;
        private DaySchedule friday;
        private DaySchedule saturday;
        private DaySchedule sunday;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DaySchedule {
        private boolean isOpen;
        private LocalTime openTime;
        private LocalTime closeTime;
        private String notes; // Special notes like "Half day", "Emergency only"
    }

    // Helper methods
    public void addDoctor(Doctor doctor) {
        if (this.doctors == null) {
            this.doctors = new ArrayList<>();
        }
        if (!this.doctors.contains(doctor)) {
            this.doctors.add(doctor);
        }
    }

    public void removeDoctor(Doctor doctor) {
        if (this.doctors != null) {
            this.doctors.remove(doctor);
        }
    }

    public boolean isQueueFull() {
        return currentQueueLength >= maxQueueCapacity;
    }

    public void incrementQueue() {
        this.currentQueueLength++;
    }

    public void decrementQueue() {
        if (this.currentQueueLength > 0) {
            this.currentQueueLength--;
        }
    }

    public void updateRating(double newRating) {
        double totalRatingPoints = this.rating * this.totalReviews;
        totalRatingPoints += newRating;
        this.totalReviews++;
        this.rating = Math.round((totalRatingPoints / this.totalReviews) * 10.0) / 10.0;
    }

    public int getAvailableDoctorsCount() {
        if (doctors == null) return 0;
        return (int) doctors.stream()
                .filter(doctor -> doctor.getAvailabilityStatus() == Doctor.AvailabilityStatus.AVAILABLE)
                .count();
    }
}