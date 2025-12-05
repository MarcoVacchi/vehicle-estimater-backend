package org.lessons.vehicles.java.quoted.service;

import java.math.BigDecimal;
import java.time.Year;

import org.lessons.vehicles.java.vehicle.model.Vehicle;
import org.lessons.vehicles.java.vehicleVariation.dto.VehicleVariationDTO;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculatorService {

    public BigDecimal calculateVehiclePrice(Vehicle vehicle, VehicleVariationDTO variation) {
        BigDecimal price = vehicle.getBasePrice() != null
                ? vehicle.getBasePrice()
                : BigDecimal.ZERO;

        int cc = variation != null && variation.cc() != null
                ? variation.cc()
                : 0;
        int extraBlocks = Math.max(0, (cc - 1000) / 500);
        BigDecimal ccMultiplier = BigDecimal.valueOf(1 + 0.05 * extraBlocks);
        price = price.multiply(ccMultiplier);

        int currentYear = Year.now().getValue();
        int immYear = variation != null && variation.immatricolationYear() != null
                ? variation.immatricolationYear()
                : currentYear;

        if (immYear == currentYear) {
        } else if (immYear >= currentYear - 2) {
            price = price.multiply(BigDecimal.valueOf(1.04));
        } else if (immYear >= currentYear - 4) {
            price = price.multiply(BigDecimal.valueOf(1.08));
        } else {
            price = price.multiply(BigDecimal.valueOf(1.12));
        }

        String fuelEn = variation != null && variation.fuelSystemEn() != null
                ? variation.fuelSystemEn().toLowerCase()
                : "";
        String fuelIt = variation != null && variation.fuelSystemIt() != null
                ? variation.fuelSystemIt().toLowerCase()
                : "";

        if ("diesel".equals(fuelEn) || "diesel".equals(fuelIt)) {
            price = price.multiply(BigDecimal.valueOf(1.03));
        } else if ("electric".equals(fuelEn) || "elettrica".equals(fuelIt)) {
            price = price.multiply(BigDecimal.valueOf(1.10));
        } else if ("hybrid".equals(fuelEn) || "ibrida".equals(fuelIt)) {
            price = price.multiply(BigDecimal.valueOf(1.05));
        } else if ("gpl".equals(fuelEn) || "gpl".equals(fuelIt)) {
            price = price.multiply(BigDecimal.valueOf(1.05));
        }

        return price;
    }
}