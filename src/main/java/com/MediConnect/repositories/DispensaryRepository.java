package com.MediConnect.repositories;

import com.MediConnect.models.Dispensary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DispensaryRepository extends MongoRepository<Dispensary, String> {
    List<Dispensary> findByCity(String city);
    List<Dispensary> findByIsOpenTrue();
    List<Dispensary> findByLocationNear(Point location, Distance distance);
}