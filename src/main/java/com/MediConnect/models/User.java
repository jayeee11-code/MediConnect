package com.MediConnect.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    @Indexed(unique = true)
    private String phoneNumber;

    private String firstName;
    private String lastName;

    private UserRole role; // PATIENT, DOCTOR, DISPENSARY_ADMIN

    private boolean isActive = true;
    private boolean isVerified = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum UserRole {
        PATIENT,
        DOCTOR,
        DISPENSARY_ADMIN
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasRole(UserRole role) {
        return this.role == role;
    }
}


