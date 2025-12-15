package com.MediConnect.controllers;

import com.MediConnect.MediConnect.dto.DispensaryDTO;
import com.MediConnect.services.DispensaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dispensaries")
@RequiredArgsConstructor
@Tag(name = "Dispensaries", description = "Dispensary management endpoints")
public class DispensaryController {

    private final DispensaryService dispensaryService;

    @GetMapping("/{id}")
    @Operation(summary = "Get dispensary by ID")
    public ResponseEntity<DispensaryDTO> getDispensaryById(@PathVariable String id) {
        return ResponseEntity.ok(dispensaryService.getDispensaryById(id));
    }

    @GetMapping
    @Operation(summary = "Get all dispensaries")
    public ResponseEntity<List<DispensaryDTO>> getAllDispensaries() {
        return ResponseEntity.ok(dispensaryService.getAllDispensaries());
    }

    @GetMapping("/open")
    @Operation(summary = "Get all open dispensaries")
    public ResponseEntity<List<DispensaryDTO>> getOpenDispensaries() {
        return ResponseEntity.ok(dispensaryService.getOpenDispensaries());
    }

    @GetMapping("/nearby")
    @Operation(summary = "Get nearby dispensaries")
    public ResponseEntity<List<DispensaryDTO>> getNearbyDispensaries(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radiusKm) {
        return ResponseEntity.ok(dispensaryService.getNearbyDispensaries(latitude, longitude, radiusKm));
    }

    @GetMapping("/search")
    @Operation(summary = "Search dispensaries by city")
    public ResponseEntity<List<DispensaryDTO>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(dispensaryService.searchByCity(city));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DISPENSARY_ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update dispensary")
    public ResponseEntity<DispensaryDTO> updateDispensary(
            @PathVariable String id,
            @RequestBody DispensaryDTO dispensaryDTO) {
        return ResponseEntity.ok(dispensaryService.updateDispensary(id, dispensaryDTO));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('DISPENSARY_ADMIN') or hasRole('DOCTOR')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update dispensary open/close status")
    public ResponseEntity<DispensaryDTO> updateOpenStatus(
            @PathVariable String id,
            @RequestParam boolean isOpen) {
        return ResponseEntity.ok(dispensaryService.updateOpenStatus(id, isOpen));
    }
}