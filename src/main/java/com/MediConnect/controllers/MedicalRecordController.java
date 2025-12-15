package com.MediConnect.controllers;

import com.MediConnect.MediConnect.dto.MedicalRecordDTO;
import com.MediConnect.services.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Medical Records", description = "Medical record management endpoints")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Create medical record")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(@RequestBody MedicalRecordDTO recordDTO) {
        return ResponseEntity.ok(medicalRecordService.createMedicalRecord(recordDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('DISPENSARY_ADMIN')")
    @Operation(summary = "Get medical record by ID")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordById(@PathVariable String id) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordById(id));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    @Operation(summary = "Get patient medical records")
    public ResponseEntity<List<MedicalRecordDTO>> getPatientMedicalRecords(@PathVariable String patientId) {
        return ResponseEntity.ok(medicalRecordService.getPatientMedicalRecords(patientId));
    }

    @GetMapping("/patient/{patientId}/date-range")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    @Operation(summary = "Get patient records by date range")
    public ResponseEntity<List<MedicalRecordDTO>> getPatientRecordsByDateRange(
            @PathVariable String patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(medicalRecordService.getPatientRecordsByDateRange(patientId, start, end));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get doctor medical records")
    public ResponseEntity<List<MedicalRecordDTO>> getDoctorMedicalRecords(@PathVariable String doctorId) {
        return ResponseEntity.ok(medicalRecordService.getDoctorMedicalRecords(doctorId));
    }

    @GetMapping("/dispensary/{dispensaryId}")
    @PreAuthorize("hasRole('DISPENSARY_ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Get dispensary medical records")
    public ResponseEntity<List<MedicalRecordDTO>> getDispensaryMedicalRecords(@PathVariable String dispensaryId) {
        return ResponseEntity.ok(medicalRecordService.getDispensaryMedicalRecords(dispensaryId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Update medical record")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(
            @PathVariable String id,
            @RequestBody MedicalRecordDTO recordDTO) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(id, recordDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('DISPENSARY_ADMIN')")
    @Operation(summary = "Delete medical record")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable String id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}