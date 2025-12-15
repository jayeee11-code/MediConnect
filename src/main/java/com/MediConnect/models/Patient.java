// ==================== Patient.java ====================
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class Patient {
    @Id
    private String id;

    @DBRef
    private User user;

    private LocalDate dateOfBirth;
    private String gender; // MALE, FEMALE, OTHER
    private String bloodGroup; // A+, A-, B+, B-, O+, O-, AB+, AB-

    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Geolocation for finding nearby dispensaries
    private Double latitude;
    private Double longitude;

    // Medical Information
    private List<String> allergies = new ArrayList<>();
    private List<String> chronicConditions = new ArrayList<>();
    private List<String> currentMedications = new ArrayList<>();

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // Additional Information
    private String occupation;
    private String insurance;
    private String insuranceNumber;

    private String profileImageUrl;
    private String notes;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Helper methods
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean hasAllergies() {
        return allergies != null && !allergies.isEmpty();
    }

    public boolean hasChronicConditions() {
        return chronicConditions != null && !chronicConditions.isEmpty();
    }
}