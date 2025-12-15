package com.MediConnect.MediConnect.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MedicalRecordDTO {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String dispensaryId;
    private LocalDateTime visitDate;
    private String chiefComplaint;
    private String symptoms;
    private String diagnosis;
    private String notes;
    private VitalSignsDTO vitalSigns;
    private List<PrescriptionDTO> prescriptions;
    private List<String> labTests;
    private String followUpInstructions;
    private LocalDateTime followUpDate;

    @Data
    public static class VitalSignsDTO {
        private Double temperature;
        private String bloodPressure;
        private Integer heartRate;
        private Integer respiratoryRate;
        private Double weight;
        private Double height;
        private Double oxygenSaturation;
    }

    @Data
    public static class PrescriptionDTO {
        private String medicationName;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}