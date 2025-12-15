package com.MediConnect.services;

import com.MediConnect.MediConnect.dto.DispensaryDTO;
import com.MediConnect.exceptions.ResourceNotFoundException;
import com.MediConnect.exceptions.ValidationException;
import com.MediConnect.models.Dispensary;
import com.MediConnect.models.Doctor;
import com.MediConnect.models.User;
import com.MediConnect.repositories.DispensaryRepository;
import com.MediConnect.repositories.DoctorRepository;
import com.MediConnect.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispensaryService {

    private final DispensaryRepository dispensaryRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    /**
     * Get dispensary by ID
     */
    public DispensaryDTO getDispensaryById(String id) {
        log.info("Fetching dispensary with ID: {}", id);
        Dispensary dispensary = dispensaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found with ID: " + id));
        return convertToDTO(dispensary);
    }

    /**
     * Get all dispensaries
     */
    public List<DispensaryDTO> getAllDispensaries() {
        log.info("Fetching all dispensaries");
        List<Dispensary> dispensaries = dispensaryRepository.findAll();
        return dispensaries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all currently open dispensaries
     */
    public List<DispensaryDTO> getOpenDispensaries() {
        log.info("Fetching all open dispensaries");
        List<Dispensary> openDispensaries = dispensaryRepository.findByIsOpenTrue();
        return openDispensaries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find nearby dispensaries based on geolocation
     */
    public List<DispensaryDTO> getNearbyDispensaries(double latitude, double longitude, double radiusKm) {
        log.info("Searching for dispensaries near lat: {}, long: {} within {} km", latitude, longitude, radiusKm);

        // MongoDB uses [longitude, latitude] order (GeoJSON format)
        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);

        List<Dispensary> nearbyDispensaries = dispensaryRepository.findByLocationNear(location, distance);

        log.info("Found {} dispensaries within {} km", nearbyDispensaries.size(), radiusKm);

        return nearbyDispensaries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search dispensaries by city
     */
    public List<DispensaryDTO> searchByCity(String city) {
        log.info("Searching dispensaries in city: {}", city);
        List<Dispensary> dispensaries = dispensaryRepository.findByCity(city);
        return dispensaries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new dispensary
     */
    @Transactional
    public DispensaryDTO createDispensary(DispensaryDTO dispensaryDTO, String adminUserId) {
        log.info("Creating new dispensary: {}", dispensaryDTO.getName());

        // Validate admin user
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        if (adminUser.getRole() != User.UserRole.DISPENSARY_ADMIN) {
            throw new ValidationException("User must have DISPENSARY_ADMIN role");
        }

        Dispensary dispensary = new Dispensary();
        dispensary.setName(dispensaryDTO.getName());
        dispensary.setLicenseNumber(generateLicenseNumber());
        dispensary.setAdminUser(adminUser);
        dispensary.setAddress(dispensaryDTO.getAddress());
        dispensary.setCity(dispensaryDTO.getCity());
        dispensary.setPhoneNumber(dispensaryDTO.getPhoneNumber());
        dispensary.setEmail(dispensaryDTO.getEmail());

        // Set location (MongoDB uses [longitude, latitude])
        if (dispensaryDTO.getLongitude() != null && dispensaryDTO.getLatitude() != null) {
            dispensary.setLocation(new double[]{dispensaryDTO.getLongitude(), dispensaryDTO.getLatitude()});
        }

        dispensary.setServices(dispensaryDTO.getServices());
        dispensary.setFacilities(dispensaryDTO.getFacilities());
        dispensary.setOpen(false);
        dispensary.setCurrentQueueLength(0);
        dispensary.setRating(0.0);
        dispensary.setTotalReviews(0);

        Dispensary savedDispensary = dispensaryRepository.save(dispensary);
        log.info("Dispensary created successfully with ID: {}", savedDispensary.getId());

        return convertToDTO(savedDispensary);
    }

    /**
     * Update dispensary information
     */
    @Transactional
    public DispensaryDTO updateDispensary(String id, DispensaryDTO dispensaryDTO) {
        log.info("Updating dispensary with ID: {}", id);

        Dispensary dispensary = dispensaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found with ID: " + id));

        // Update basic information
        if (dispensaryDTO.getName() != null) {
            dispensary.setName(dispensaryDTO.getName());
        }
        if (dispensaryDTO.getAddress() != null) {
            dispensary.setAddress(dispensaryDTO.getAddress());
        }
        if (dispensaryDTO.getCity() != null) {
            dispensary.setCity(dispensaryDTO.getCity());
        }
        if (dispensaryDTO.getPhoneNumber() != null) {
            dispensary.setPhoneNumber(dispensaryDTO.getPhoneNumber());
        }
        if (dispensaryDTO.getEmail() != null) {
            dispensary.setEmail(dispensaryDTO.getEmail());
        }

        // Update location if both coordinates are provided
        if (dispensaryDTO.getLatitude() != null && dispensaryDTO.getLongitude() != null) {
            dispensary.setLocation(new double[]{dispensaryDTO.getLongitude(), dispensaryDTO.getLatitude()});
        }

        // Update services and facilities
        if (dispensaryDTO.getServices() != null) {
            dispensary.setServices(dispensaryDTO.getServices());
        }
        if (dispensaryDTO.getFacilities() != null) {
            dispensary.setFacilities(dispensaryDTO.getFacilities());
        }

        Dispensary updatedDispensary = dispensaryRepository.save(dispensary);
        log.info("Dispensary updated successfully: {}", id);

        return convertToDTO(updatedDispensary);
    }

    /**
     * Update dispensary open/close status
     */
    @Transactional
    public DispensaryDTO updateOpenStatus(String id, boolean isOpen) {
        log.info("Updating dispensary {} status to: {}", id, isOpen ? "OPEN" : "CLOSED");

        Dispensary dispensary = dispensaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found with ID: " + id));

        dispensary.setOpen(isOpen);

        // If closing, reset queue length
        if (!isOpen) {
            dispensary.setCurrentQueueLength(0);
            log.info("Reset queue length for closed dispensary");
        }

        Dispensary updated = dispensaryRepository.save(dispensary);
        log.info("Dispensary status updated successfully");

        return convertToDTO(updated);
    }

    /**
     * Update dispensary working hours
     */
    @Transactional
    public DispensaryDTO updateWorkingHours(String id, Dispensary.WorkingHours workingHours) {
        log.info("Updating working hours for dispensary: {}", id);

        Dispensary dispensary = dispensaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found with ID: " + id));

        dispensary.setWorkingHours(workingHours);

        Dispensary updated = dispensaryRepository.save(dispensary);
        log.info("Working hours updated successfully");

        return convertToDTO(updated);
    }

    /**
     * Add doctor to dispensary
     */
    @Transactional
    public DispensaryDTO addDoctor(String dispensaryId, String doctorId) {
        log.info("Adding doctor {} to dispensary {}", doctorId, dispensaryId);

        Dispensary dispensary = dispensaryRepository.findById(dispensaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (dispensary.getDoctors() == null) {
            dispensary.setDoctors(List.of(doctor));
        } else if (!dispensary.getDoctors().contains(doctor)) {
            dispensary.getDoctors().add(doctor);
        } else {
            throw new ValidationException("Doctor already associated with this dispensary");
        }

        Dispensary updated = dispensaryRepository.save(dispensary);
        log.info("Doctor added successfully");

        return convertToDTO(updated);
    }

    /**
     * Remove doctor from dispensary
     */
    @Transactional
    public DispensaryDTO removeDoctor(String dispensaryId, String doctorId) {
        log.info("Removing doctor {} from dispensary {}", doctorId, dispensaryId);

        Dispensary dispensary = dispensaryRepository.findById(dispensaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found"));

        if (dispensary.getDoctors() != null) {
            dispensary.getDoctors().removeIf(doctor -> doctor.getId().equals(doctorId));
        }

        Dispensary updated = dispensaryRepository.save(dispensary);
        log.info("Doctor removed successfully");

        return convertToDTO(updated);
    }

    /**
     * Update queue length
     */
    @Transactional
    public void updateQueueLength(String id, int queueLength) {
        log.info("Updating queue length for dispensary {}: {}", id, queueLength);

        Dispensary dispensary = dispensaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found"));

        dispensary.setCurrentQueueLength(Math.max(0, queueLength));
        dispensaryRepository.save(dispensary);
    }

    /**
     * Update rating
     */
    @Transactional
    public DispensaryDTO updateRating(String id, double newRating) {
        log.info("Adding rating {} for dispensary {}", newRating, id);

        Dispensary dispensary = dispensaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found"));

        // Calculate new average rating
        double currentRating = dispensary.getRating() != null ? dispensary.getRating() : 0.0;
        int totalReviews = dispensary.getTotalReviews() != null ? dispensary.getTotalReviews() : 0;

        double totalRatingPoints = currentRating * totalReviews;
        totalRatingPoints += newRating;
        totalReviews += 1;

        double averageRating = totalRatingPoints / totalReviews;

        dispensary.setRating(Math.round(averageRating * 10.0) / 10.0); // Round to 1 decimal
        dispensary.setTotalReviews(totalReviews);

        Dispensary updated = dispensaryRepository.save(dispensary);
        log.info("Rating updated. New average: {}, Total reviews: {}", updated.getRating(), updated.getTotalReviews());

        return convertToDTO(updated);
    }

    /**
     * Delete dispensary
     */
    @Transactional
    public void deleteDispensary(String id) {
        log.info("Deleting dispensary: {}", id);

        if (!dispensaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dispensary not found with ID: " + id);
        }

        dispensaryRepository.deleteById(id);
        log.info("Dispensary deleted successfully");
    }

    /**
     * Check if dispensary is currently open based on working hours
     */
    public boolean isDispensaryOpen(String id) {
        Dispensary dispensary = dispensaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found"));

        if (!dispensary.isOpen()) {
            return false;
        }

        // Check working hours
        if (dispensary.getWorkingHours() == null) {
            return true; // Assume 24/7 if no hours specified
        }

        LocalTime now = LocalTime.now();
        String dayOfWeek = LocalDateTime.now().getDayOfWeek().toString().toLowerCase();

        // Get today's schedule
        Dispensary.DaySchedule schedule = getTodaySchedule(dispensary.getWorkingHours(), dayOfWeek);

        if (schedule == null || !schedule.isOpen()) {
            return false;
        }

        return !now.isBefore(schedule.getOpenTime()) && !now.isAfter(schedule.getCloseTime());
    }

    /**
     * Helper method to get today's schedule
     */
    private Dispensary.DaySchedule getTodaySchedule(Dispensary.WorkingHours hours, String day) {
        return switch (day) {
            case "monday" -> hours.getMonday();
            case "tuesday" -> hours.getTuesday();
            case "wednesday" -> hours.getWednesday();
            case "thursday" -> hours.getThursday();
            case "friday" -> hours.getFriday();
            case "saturday" -> hours.getSaturday();
            case "sunday" -> hours.getSunday();
            default -> null;
        };
    }

    /**
     * Generate unique license number
     */
    private String generateLicenseNumber() {
        return "DISP-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    /**
     * Convert Dispensary entity to DTO
     */
    private DispensaryDTO convertToDTO(Dispensary dispensary) {
        DispensaryDTO dto = new DispensaryDTO();
        dto.setId(dispensary.getId());
        dto.setName(dispensary.getName());
        dto.setAddress(dispensary.getAddress());
        dto.setCity(dispensary.getCity());
        dto.setPhoneNumber(dispensary.getPhoneNumber());
        dto.setEmail(dispensary.getEmail());

        // Convert location from [longitude, latitude] to separate fields
        if (dispensary.getLocation() != null && dispensary.getLocation().length == 2) {
            dto.setLongitude(dispensary.getLocation()[0]);
            dto.setLatitude(dispensary.getLocation()[1]);
        }

        dto.setServices(dispensary.getServices());
        dto.setFacilities(dispensary.getFacilities());
        dto.setOpen(dispensary.isOpen());
        dto.setCurrentQueueLength(dispensary.getCurrentQueueLength());
        dto.setRating(dispensary.getRating());
        dto.setTotalReviews(dispensary.getTotalReviews());

        // Convert doctors to summary format
        if (dispensary.getDoctors() != null && !dispensary.getDoctors().isEmpty()) {
            List<DispensaryDTO.DoctorSummary> doctorSummaries = dispensary.getDoctors().stream()
                    .map(doctor -> {
                        DispensaryDTO.DoctorSummary summary = new DispensaryDTO.DoctorSummary();
                        summary.setId(doctor.getId());

                        User user = doctor.getUser();
                        if (user != null) {
                            summary.setName("Dr. " + user.getFirstName() + " " + user.getLastName());
                        }

                        summary.setSpecialization(doctor.getSpecialization());

                        if (doctor.getAvailabilityStatus() != null) {
                            summary.setAvailabilityStatus(doctor.getAvailabilityStatus().name());
                        }

                        return summary;
                    })
                    .collect(Collectors.toList());

            dto.setAvailableDoctors(doctorSummaries);
        }

        return dto;
    }
}