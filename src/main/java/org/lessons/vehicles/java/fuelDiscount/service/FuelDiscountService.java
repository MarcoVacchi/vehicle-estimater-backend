package org.lessons.vehicles.java.fuelDiscount.service;

import java.util.List;

import org.lessons.vehicles.java.fuelDiscount.dto.FuelDiscountDTO;
import org.lessons.vehicles.java.fuelDiscount.model.FuelDiscount;
import org.lessons.vehicles.java.fuelDiscount.repository.FuelDiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuelDiscountService {

    @Autowired
    private FuelDiscountRepository fuelDiscountRepository;

    public FuelDiscountService(FuelDiscountRepository fuelDiscountRepository) {
        this.fuelDiscountRepository = fuelDiscountRepository;
    }

    public List<FuelDiscountDTO> getAllDiscounts() {
        return fuelDiscountRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    private FuelDiscountDTO toDTO(FuelDiscount fuelDiscount) {
        if (fuelDiscount == null) {
            return null;
        }

        return new FuelDiscountDTO(
                fuelDiscount.getName(),
                fuelDiscount.getDiscount());
    }
}
