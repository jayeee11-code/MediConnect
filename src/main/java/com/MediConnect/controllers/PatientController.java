package com.MediConnect.controllers;

import com.MediConnect.MediConnect.dto.PatientDTO;
import com.MediConnect.security.CustomUserDetails;
import com.MediConnect.services.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get current patient profile")
    public ResponseEntity<PatientDTO> getCurrentPatient(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.getPatientByUserId(userDetails.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('DISPENSARY_ADMIN')")
    @Operation(summary = "Get all patients")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Update patient profile")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable String id,
            @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Delete patient profile")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}