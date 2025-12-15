package com.MediConnect.MediConnect.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientDTO {
    private String id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private List<String> allergies;
    private List<String> chronicConditions;
    private String emergencyContactName;
    private String emergencyContactPhone;
}