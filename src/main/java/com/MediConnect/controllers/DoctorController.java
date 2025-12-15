package com.MediConnect.controllers;

import com.MediConnect.MediConnect.dto.DoctorDTO;
import com.MediConnect.security.CustomUserDetails;
import com.MediConnect.services.DoctorService;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Doctors", description = "Doctor management endpoints")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable String id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get current doctor profile")
    public ResponseEntity<DoctorDTO> getCurrentDoctor(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(doctorService.getDoctorByUserId(userDetails.getId()));
    }

    @GetMapping
    @Operation(summary = "Get all doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Get doctors by specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Update doctor profile")
    public ResponseEntity<DoctorDTO> updateDoctor(
            @PathVariable String id,
            @RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorDTO));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Update doctor availability status")
    public ResponseEntity<DoctorDTO> updateAvailability(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(doctorService.updateAvailability(id, status));
    }
}