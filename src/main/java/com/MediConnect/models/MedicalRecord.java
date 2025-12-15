package com.MediConnect.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medical_records")
public class  MedicalRecord {
    @Id
    private String id;

    @DBRef
    private Patient patient;

    @DBRef
    private Doctor doctor;

    @DBRef
    private Dispensary dispensary;

    private LocalDateTime visitDate;

    private String chiefComplaint;
    private String symptoms;
    private String diagnosis;
    private String notes;

    private VitalSigns vitalSigns;

    private List<Prescription> prescriptions;
    private List<String> labTests;

    private String followUpInstructions;
    private LocalDateTime followUpDate;

    private List<String> attachments; // URLs to files

    @CreatedDate
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VitalSigns {
        private Double temperature; // Celsius
        private String bloodPressure; // e.g., "120/80"
        private Integer heartRate; // bpm
        private Integer respiratoryRate;
        private Double weight; // kg
        private Double height; // cm
        private Double oxygenSaturation; // percentage
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prescription {
        private String medicationName;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}