package com.MediConnect.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueueJoinRequest {
    @NotBlank(message = "Dispensary ID is required")
    private String dispensaryId;

    private String doctorId;
    private String chiefComplaint;
    private String notes;
}