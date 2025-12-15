package com.MediConnect.services;

import com.MediConnect.MediConnect.dto.MedicalRecordDTO;
import com.MediConnect.exceptions.ResourceNotFoundException;
import com.MediConnect.models.*;
import com.MediConnect.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DispensaryRepository dispensaryRepository;

    @Transactional
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO recordDTO) {
        Patient patient = patientRepository.findById(recordDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(recordDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Dispensary dispensary = dispensaryRepository.findById(recordDTO.getDispensaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found"));

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setDispensary(dispensary);
        record.setVisitDate(LocalDateTime.now());
        record.setChiefComplaint(recordDTO.getChiefComplaint());
        record.setSymptoms(recordDTO.getSymptoms());
        record.setDiagnosis(recordDTO.getDiagnosis());
        record.setNotes(recordDTO.getNotes());

        // Set vital signs
        if (recordDTO.getVitalSigns() != null) {
            MedicalRecord.VitalSigns vitalSigns = new MedicalRecord.VitalSigns();
            MedicalRecordDTO.VitalSignsDTO dto = recordDTO.getVitalSigns();
            vitalSigns.setTemperature(dto.getTemperature());
            vitalSigns.setBloodPressure(dto.getBloodPressure());
            vitalSigns.setHeartRate(dto.getHeartRate());
            vitalSigns.setRespiratoryRate(dto.getRespiratoryRate());
            vitalSigns.setWeight(dto.getWeight());
            vitalSigns.setHeight(dto.getHeight());
            vitalSigns.setOxygenSaturation(dto.getOxygenSaturation());
            record.setVitalSigns(vitalSigns);
        }

        // Set prescriptions
        if (recordDTO.getPrescriptions() != null) {
            List<MedicalRecord.Prescription> prescriptions = recordDTO.getPrescriptions().stream()
                    .map(dto -> {
                        MedicalRecord.Prescription p = new MedicalRecord.Prescription();
                        p.setMedicationName(dto.getMedicationName());
                        p.setDosage(dto.getDosage());
                        p.setFrequency(dto.getFrequency());
                        p.setDuration(dto.getDuration());
                        p.setInstructions(dto.getInstructions());
                        return p;
                    })
                    .collect(Collectors.toList());
            record.setPrescriptions(prescriptions);
        }

        record.setLabTests(recordDTO.getLabTests());
        record.setFollowUpInstructions(recordDTO.getFollowUpInstructions());
        record.setFollowUpDate(recordDTO.getFollowUpDate());

        MedicalRecord saved = medicalRecordRepository.save(record);
        return convertToDTO(saved);
    }

    public MedicalRecordDTO getMedicalRecordById(String id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found"));
        return convertToDTO(record);
    }

    public List<MedicalRecordDTO> getPatientMedicalRecords(String patientId) {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId);
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<MedicalRecordDTO> getDoctorMedicalRecords(String doctorId) {
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorIdOrderByVisitDateDesc(doctorId);
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<MedicalRecordDTO> getDispensaryMedicalRecords(String dispensaryId) {
        List<MedicalRecord> records = medicalRecordRepository.findByDispensaryIdOrderByVisitDateDesc(dispensaryId);
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<MedicalRecordDTO> getPatientRecordsByDateRange(String patientId, LocalDateTime start, LocalDateTime end) {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientIdAndVisitDateBetween(patientId, start, end);
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public MedicalRecordDTO updateMedicalRecord(String id, MedicalRecordDTO recordDTO) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found"));

        record.setChiefComplaint(recordDTO.getChiefComplaint());
        record.setSymptoms(recordDTO.getSymptoms());
        record.setDiagnosis(recordDTO.getDiagnosis());
        record.setNotes(recordDTO.getNotes());

        // Update vital signs
        if (recordDTO.getVitalSigns() != null) {
            MedicalRecord.VitalSigns vitalSigns = new MedicalRecord.VitalSigns();
            MedicalRecordDTO.VitalSignsDTO dto = recordDTO.getVitalSigns();
            vitalSigns.setTemperature(dto.getTemperature());
            vitalSigns.setBloodPressure(dto.getBloodPressure());
            vitalSigns.setHeartRate(dto.getHeartRate());
            vitalSigns.setRespiratoryRate(dto.getRespiratoryRate());
            vitalSigns.setWeight(dto.getWeight());
            vitalSigns.setHeight(dto.getHeight());
            vitalSigns.setOxygenSaturation(dto.getOxygenSaturation());
            record.setVitalSigns(vitalSigns);
        }

        // Update prescriptions
        if (recordDTO.getPrescriptions() != null) {
            List<MedicalRecord.Prescription> prescriptions = recordDTO.getPrescriptions().stream()
                    .map(dto -> {
                        MedicalRecord.Prescription p = new MedicalRecord.Prescription();
                        p.setMedicationName(dto.getMedicationName());
                        p.setDosage(dto.getDosage());
                        p.setFrequency(dto.getFrequency());
                        p.setDuration(dto.getDuration());
                        p.setInstructions(dto.getInstructions());
                        return p;
                    })
                    .collect(Collectors.toList());
            record.setPrescriptions(prescriptions);
        }

        record.setLabTests(recordDTO.getLabTests());
        record.setFollowUpInstructions(recordDTO.getFollowUpInstructions());
        record.setFollowUpDate(recordDTO.getFollowUpDate());

        MedicalRecord updated = medicalRecordRepository.save(record);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteMedicalRecord(String id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medical record not found");
        }
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDTO convertToDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());

        if (record.getPatient() != null) {
            dto.setPatientId(record.getPatient().getId());
            User patientUser = record.getPatient().getUser();
            dto.setPatientName(patientUser.getFirstName() + " " + patientUser.getLastName());
        }

        if (record.getDoctor() != null) {
            dto.setDoctorId(record.getDoctor().getId());
            User doctorUser = record.getDoctor().getUser();
            dto.setDoctorName("Dr. " + doctorUser.getFirstName() + " " + doctorUser.getLastName());
        }

        if (record.getDispensary() != null) {
            dto.setDispensaryId(record.getDispensary().getId());
        }

        dto.setVisitDate(record.getVisitDate());
        dto.setChiefComplaint(record.getChiefComplaint());
        dto.setSymptoms(record.getSymptoms());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setNotes(record.getNotes());

        // Convert vital signs
        if (record.getVitalSigns() != null) {
            MedicalRecordDTO.VitalSignsDTO vitalSignsDTO = new MedicalRecordDTO.VitalSignsDTO();
            MedicalRecord.VitalSigns vs = record.getVitalSigns();
            vitalSignsDTO.setTemperature(vs.getTemperature());
            vitalSignsDTO.setBloodPressure(vs.getBloodPressure());
            vitalSignsDTO.setHeartRate(vs.getHeartRate());
            vitalSignsDTO.setRespiratoryRate(vs.getRespiratoryRate());
            vitalSignsDTO.setWeight(vs.getWeight());
            vitalSignsDTO.setHeight(vs.getHeight());
            vitalSignsDTO.setOxygenSaturation(vs.getOxygenSaturation());
            dto.setVitalSigns(vitalSignsDTO);
        }

        // Convert prescriptions
        if (record.getPrescriptions() != null) {
            List<MedicalRecordDTO.PrescriptionDTO> prescriptionDTOs = record.getPrescriptions().stream()
                    .map(p -> {
                        MedicalRecordDTO.PrescriptionDTO pDto = new MedicalRecordDTO.PrescriptionDTO();
                        pDto.setMedicationName(p.getMedicationName());
                        pDto.setDosage(p.getDosage());
                        pDto.setFrequency(p.getFrequency());
                        pDto.setDuration(p.getDuration());
                        pDto.setInstructions(p.getInstructions());
                        return pDto;
                    })
                    .collect(Collectors.toList());
            dto.setPrescriptions(prescriptionDTOs);
        }

        dto.setLabTests(record.getLabTests());
        dto.setFollowUpInstructions(record.getFollowUpInstructions());
        dto.setFollowUpDate(record.getFollowUpDate());

        return dto;
    }
}