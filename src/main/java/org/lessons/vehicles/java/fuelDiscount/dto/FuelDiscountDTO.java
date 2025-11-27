package org.lessons.vehicles.java.fuelDiscount.dto;

import java.math.BigDecimal;

public record FuelDiscountDTO(
        String name,
        BigDecimal discount) {

}
