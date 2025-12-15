package com.MediConnect.repositories;

import com.MediConnect.models.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(String patientId);
    List<MedicalRecord> findByDoctorIdOrderByVisitDateDesc(String doctorId);
    List<MedicalRecord> findByDispensaryIdOrderByVisitDateDesc(String dispensaryId);
    List<MedicalRecord> findByPatientIdAndVisitDateBetween(String patientId, LocalDateTime start, LocalDateTime end);
}