package com.MediConnect.repositories;

import com.MediConnect.models.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByUserId(String userId);
}
