package org.lessons.vehicles.java.vehicle.repository;

import org.lessons.vehicles.java.vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

}
