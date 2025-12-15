package com.MediConnect.repositories;

import com.MediConnect.models.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Optional<Doctor> findByUserId(String userId);
    List<Doctor> findBySpecialization(String specialization);
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}