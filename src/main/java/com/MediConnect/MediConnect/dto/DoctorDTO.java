package com.MediConnect.MediConnect.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DoctorDTO {
    private String id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String qualification;
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private List<String> languages;
    private String bio;
    private String availabilityStatus;
    private LocalDateTime statusLastUpdated;
    private Double consultationFee;
    private Integer averageConsultationTime;
}