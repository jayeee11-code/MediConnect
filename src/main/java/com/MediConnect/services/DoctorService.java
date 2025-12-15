package com.MediConnect.services;

import com.MediConnect.MediConnect.dto.DoctorDTO;
import com.MediConnect.exceptions.ResourceNotFoundException;
import com.MediConnect.models.Doctor;
import com.MediConnect.models.User;
import com.MediConnect.repositories.DoctorRepository;
import com.MediConnect.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorDTO getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return convertToDTO(doctor);
    }

    public DoctorDTO getDoctorByUserId(String userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user"));
        return convertToDTO(doctor);
    }

    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DoctorDTO updateDoctor(String id, DoctorDTO doctorDTO) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // Update user profile details if provided
        User user = doctor.getUser();
        if (user != null) {
            boolean userUpdated = false;
            if (doctorDTO.getFirstName() != null) {
                user.setFirstName(doctorDTO.getFirstName());
                userUpdated = true;
            }
            if (doctorDTO.getLastName() != null) {
                user.setLastName(doctorDTO.getLastName());
                userUpdated = true;
            }
            if (doctorDTO.getPhoneNumber() != null) {
                user.setPhoneNumber(doctorDTO.getPhoneNumber());
                userUpdated = true;
            }
            if (userUpdated) {
                userRepository.save(user);
            }
        }

        // Update doctor fields
        if (doctorDTO.getQualification() != null) {
            doctor.setQualification(doctorDTO.getQualification());
        }
        if (doctorDTO.getSpecialization() != null) {
            doctor.setSpecialization(doctorDTO.getSpecialization());
        }
        if (doctorDTO.getLicenseNumber() != null) {
            doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
        }
        if (doctorDTO.getYearsOfExperience() != null) {
            doctor.setYearsOfExperience(doctorDTO.getYearsOfExperience());
        }
        if (doctorDTO.getLanguages() != null) {
            doctor.setLanguages(doctorDTO.getLanguages());
        }
        if (doctorDTO.getBio() != null) {
            doctor.setBio(doctorDTO.getBio());
        }
        if (doctorDTO.getConsultationFee() != null) {
            doctor.setConsultationFee(doctorDTO.getConsultationFee());
        }
        if (doctorDTO.getAverageConsultationTime() != null) {
            doctor.setAverageConsultationTime(doctorDTO.getAverageConsultationTime());
        }

        Doctor saved = doctorRepository.save(doctor);
        return convertToDTO(saved);
    }

    public DoctorDTO updateAvailability(String id, String status) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Doctor.AvailabilityStatus availabilityStatus =
                Doctor.AvailabilityStatus.valueOf(status.toUpperCase());

        doctor.setAvailabilityStatus(availabilityStatus);
        doctor.setStatusLastUpdated(LocalDateTime.now());

        Doctor saved = doctorRepository.save(doctor);
        return convertToDTO(saved);
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());

        User user = doctor.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setPhoneNumber(user.getPhoneNumber());
        }

        dto.setQualification(doctor.getQualification());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        dto.setLanguages(doctor.getLanguages());
        dto.setBio(doctor.getBio());
        dto.setAvailabilityStatus(
                doctor.getAvailabilityStatus() != null ? doctor.getAvailabilityStatus().name() : null);
        dto.setStatusLastUpdated(doctor.getStatusLastUpdated());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setAverageConsultationTime(doctor.getAverageConsultationTime());
        return dto;
    }
}
