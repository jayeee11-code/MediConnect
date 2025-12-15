package com.MediConnect.MediConnect.dto;

import lombok.Data;
import java.util.List;

@Data
public class DispensaryDTO {
    private String id;
    private String name;
    private String address;
    private String city;
    private String phoneNumber;
    private String email;
    private Double latitude;
    private Double longitude;
    private List<String> services;
    private List<String> facilities;
    private boolean isOpen;
    private Integer currentQueueLength;
    private Double rating;
    private Integer totalReviews;
    private List<DoctorSummary> availableDoctors;

    @Data
    public static class DoctorSummary {
        private String id;
        private String name;
        private String specialization;
        private String availabilityStatus;
    }
}
