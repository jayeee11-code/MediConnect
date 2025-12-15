package com.MediConnect.controllers;

import com.MediConnect.MediConnect.dto.QueueEntryDTO;
import com.MediConnect.dto.auth.QueueJoinRequest;
import com.MediConnect.security.CustomUserDetails;
import com.MediConnect.services.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Queue", description = "Queue management endpoints")
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/join")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Join queue")
    public ResponseEntity<QueueEntryDTO> joinQueue(
            @Valid @RequestBody QueueJoinRequest request,
            @RequestParam String patientId) {
        return ResponseEntity.ok(queueService.joinQueue(patientId, request));
    }

    @GetMapping("/dispensary/{dispensaryId}")
    @Operation(summary = "Get queue by dispensary")
    public ResponseEntity<List<QueueEntryDTO>> getQueueByDispensary(@PathVariable String dispensaryId) {
        return ResponseEntity.ok(queueService.getQueueByDispensary(dispensaryId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('DISPENSARY_ADMIN')")
    @Operation(summary = "Get queue by doctor")
    public ResponseEntity<List<QueueEntryDTO>> getQueueByDoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(queueService.getQueueByDoctor(doctorId));
    }

    @GetMapping("/patient/{patientId}/history")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    @Operation(summary = "Get patient queue history")
    public ResponseEntity<List<QueueEntryDTO>> getPatientQueueHistory(@PathVariable String patientId) {
        return ResponseEntity.ok(queueService.getPatientQueueHistory(patientId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get queue entry by ID")
    public ResponseEntity<QueueEntryDTO> getQueueEntryById(@PathVariable String id) {
        return ResponseEntity.ok(queueService.getQueueEntryById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('DISPENSARY_ADMIN')")
    @Operation(summary = "Update queue entry status")
    public ResponseEntity<QueueEntryDTO> updateQueueStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(queueService.updateQueueStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('DISPENSARY_ADMIN')")
    @Operation(summary = "Cancel queue entry")
    public ResponseEntity<Void> cancelQueueEntry(@PathVariable String id) {
        queueService.cancelQueueEntry(id);
        return ResponseEntity.noContent().build();
    }
}