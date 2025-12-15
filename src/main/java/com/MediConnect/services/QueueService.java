package com.MediConnect.services;

import com.MediConnect.MediConnect.dto.QueueEntryDTO;
import com.MediConnect.dto.auth.QueueJoinRequest;
import com.MediConnect.exceptions.ResourceNotFoundException;
import com.MediConnect.exceptions.ValidationException;
import com.MediConnect.models.*;
import com.MediConnect.repositories.DispensaryRepository;
import com.MediConnect.repositories.DoctorRepository;
import com.MediConnect.repositories.PatientRepository;
import com.MediConnect.repositories.QueueRepository;
import com.MediConnect.utils.QueueCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DispensaryRepository dispensaryRepository;
    private final QueueCalculator queueCalculator;

    @Transactional
    public QueueEntryDTO joinQueue(String patientId, QueueJoinRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Dispensary dispensary = dispensaryRepository.findById(request.getDispensaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Dispensary not found"));

        Doctor doctor = null;
        if (request.getDoctorId() != null) {
            doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        }

        Optional<QueueEntry> existing = queueRepository
                .findByPatientIdAndDispensaryIdAndStatus(patientId, dispensary.getId(), QueueEntry.QueueStatus.WAITING);
        if (existing.isPresent()) {
            throw new ValidationException("Patient is already in the queue for this dispensary");
        }

        int currentWaiting = queueRepository.countByDispensaryIdAndStatus(dispensary.getId(), QueueEntry.QueueStatus.WAITING);

        QueueEntry entry = new QueueEntry();
        entry.setPatient(patient);
        entry.setDispensary(dispensary);
        entry.setDoctor(doctor);
        entry.setQueueNumber(queueCalculator.calculateNextQueueNumber(currentWaiting));
        entry.setPosition(currentWaiting + 1);
        entry.setStatus(QueueEntry.QueueStatus.WAITING);
        entry.setChiefComplaint(request.getChiefComplaint());
        entry.setNotes(request.getNotes());
        entry.setJoinedAt(LocalDateTime.now());

        Integer avgConsultation = doctor != null ? doctor.getAverageConsultationTime() : null;
        int estimatedWait = queueCalculator.calculateEstimatedWaitTime(entry.getPosition(), avgConsultation);
        entry.setEstimatedWaitTime(estimatedWait);
        entry.setEstimatedCallTime(LocalDateTime.now().plusMinutes(estimatedWait));

        QueueEntry saved = queueRepository.save(entry);
        return convertToDTO(saved);
    }

    public List<QueueEntryDTO> getQueueByDispensary(String dispensaryId) {
        return queueRepository.findByDispensaryIdAndStatusOrderByPositionAsc(
                        dispensaryId, QueueEntry.QueueStatus.WAITING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QueueEntryDTO> getQueueByDoctor(String doctorId) {
        return queueRepository.findByDoctorIdAndStatusOrderByPositionAsc(
                        doctorId, QueueEntry.QueueStatus.WAITING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QueueEntryDTO> getPatientQueueHistory(String patientId) {
        return queueRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QueueEntryDTO getQueueEntryById(String id) {
        QueueEntry entry = queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found"));
        return convertToDTO(entry);
    }

    @Transactional
    public QueueEntryDTO updateQueueStatus(String id, String status) {
        QueueEntry entry = queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found"));

        QueueEntry.QueueStatus newStatus = QueueEntry.QueueStatus.valueOf(status.toUpperCase());
        entry.setStatus(newStatus);

        switch (newStatus) {
            case CALLED -> entry.setCalledAt(LocalDateTime.now());
            case IN_CONSULTATION -> entry.setConsultationStartedAt(LocalDateTime.now());
            case COMPLETED -> entry.setCompletedAt(LocalDateTime.now());
            case CANCELLED -> {
                entry.setCancelledAt(LocalDateTime.now());
                entry.setCancelledBy("SYSTEM");
            }
            default -> {
                // no-op for other statuses
            }
        }

        QueueEntry saved = queueRepository.save(entry);
        return convertToDTO(saved);
    }

    @Transactional
    public void cancelQueueEntry(String id) {
        QueueEntry entry = queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found"));
        entry.cancel("Cancelled by request", "SYSTEM");
        queueRepository.save(entry);
    }

    private QueueEntryDTO convertToDTO(QueueEntry entry) {
        QueueEntryDTO dto = new QueueEntryDTO();
        dto.setId(entry.getId());
        dto.setQueueNumber(entry.getQueueNumber());
        dto.setPosition(entry.getPosition());
        dto.setStatus(entry.getStatus() != null ? entry.getStatus().name() : null);
        dto.setChiefComplaint(entry.getChiefComplaint());
        dto.setJoinedAt(entry.getJoinedAt());
        dto.setEstimatedWaitTime(entry.getEstimatedWaitTime());

        if (entry.getPatient() != null) {
            dto.setPatientId(entry.getPatient().getId());
            User user = entry.getPatient().getUser();
            if (user != null) {
                dto.setPatientName(user.getFirstName() + " " + user.getLastName());
            }
        }

        if (entry.getDoctor() != null) {
            dto.setDoctorId(entry.getDoctor().getId());
            User doctorUser = entry.getDoctor().getUser();
            if (doctorUser != null) {
                dto.setDoctorName("Dr. " + doctorUser.getFirstName() + " " + doctorUser.getLastName());
            }
        }

        if (entry.getDispensary() != null) {
            dto.setDispensaryId(entry.getDispensary().getId());
            dto.setDispensaryName(entry.getDispensary().getName());
        }

        return dto;
    }
}
