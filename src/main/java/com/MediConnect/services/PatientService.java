package com.MediConnect.services;

import com.MediConnect.MediConnect.dto.PatientDTO;
import com.MediConnect.exceptions.ResourceNotFoundException;
import com.MediConnect.models.Patient;
import com.MediConnect.models.User;
import com.MediConnect.repositories.PatientRepository;
import com.MediConnect.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientDTO getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return convertToDTO(patient);
    }

    public PatientDTO getPatientByUserId(String userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user"));
        return convertToDTO(patient);
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO updatePatient(String id, PatientDTO patientDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Update user information if provided
        User user = patient.getUser();
        if (user != null) {
            boolean userUpdated = false;
            if (patientDTO.getFirstName() != null) {
                user.setFirstName(patientDTO.getFirstName());
                userUpdated = true;
            }
            if (patientDTO.getLastName() != null) {
                user.setLastName(patientDTO.getLastName());
                userUpdated = true;
            }
            if (patientDTO.getPhoneNumber() != null) {
                user.setPhoneNumber(patientDTO.getPhoneNumber());
                userUpdated = true;
            }
            if (userUpdated) {
                userRepository.save(user);
            }
        }

        // Update patient fields
        patient.setDateOfBirth(patientDTO.getDateOfBirth());
        patient.setGender(patientDTO.getGender());
        patient.setBloodGroup(patientDTO.getBloodGroup());
        patient.setAddress(patientDTO.getAddress());
        patient.setCity(patientDTO.getCity());
        patient.setLatitude(patientDTO.getLatitude());
        patient.setLongitude(patientDTO.getLongitude());
        patient.setAllergies(patientDTO.getAllergies());
        patient.setChronicConditions(patientDTO.getChronicConditions());
        patient.setEmergencyContactName(patientDTO.getEmergencyContactName());
        patient.setEmergencyContactPhone(patientDTO.getEmergencyContactPhone());

        Patient saved = patientRepository.save(patient);
        return convertToDTO(saved);
    }

    public void deletePatient(String id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());

        User user = patient.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setPhoneNumber(user.getPhoneNumber());
        }

        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setAddress(patient.getAddress());
        dto.setCity(patient.getCity());
        dto.setLatitude(patient.getLatitude());
        dto.setLongitude(patient.getLongitude());
        dto.setAllergies(patient.getAllergies());
        dto.setChronicConditions(patient.getChronicConditions());
        dto.setEmergencyContactName(patient.getEmergencyContactName());
        dto.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        return dto;
    }
}
