package com.MediConnect.repositories;

import com.MediConnect.models.QueueEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends MongoRepository<QueueEntry, String> {
    List<QueueEntry> findByDispensaryIdAndStatusOrderByPositionAsc(String dispensaryId, QueueEntry.QueueStatus status);
    List<QueueEntry> findByDoctorIdAndStatusOrderByPositionAsc(String doctorId, QueueEntry.QueueStatus status);
    List<QueueEntry> findByPatientIdOrderByCreatedAtDesc(String patientId);
    Optional<QueueEntry> findByPatientIdAndDispensaryIdAndStatus(String patientId, String dispensaryId, QueueEntry.QueueStatus status);
    Integer countByDispensaryIdAndStatus(String dispensaryId, QueueEntry.QueueStatus status);
    Integer countByDoctorIdAndStatus(String doctorId, QueueEntry.QueueStatus status);
}