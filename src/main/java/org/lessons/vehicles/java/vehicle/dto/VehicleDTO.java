package org.lessons.vehicles.java.vehicle.dto;

import java.math.BigDecimal;
import java.util.List;

import org.lessons.vehicles.java.vehicleVariation.dto.VehicleVariationDTO;

public record VehicleDTO(
                Integer id,
                String vehicleType,
                String brand,
                String model,
                BigDecimal basePrice,
                String img,
                List<VehicleVariationDTO> vehicleVariations) {
}
