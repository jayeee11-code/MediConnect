package com.MediConnect.MediConnect.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QueueEntryDTO {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String dispensaryId;
    private String dispensaryName;
    private Integer queueNumber;
    private Integer position;
    private String status;
    private String chiefComplaint;
    private LocalDateTime joinedAt;
    private Integer estimatedWaitTime;
}