package org.lessons.vehicles.java.fuelDiscount.repository;

import org.lessons.vehicles.java.fuelDiscount.model.FuelDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuelDiscountRepository extends JpaRepository<FuelDiscount, Integer> {

}
