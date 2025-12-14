package org.lessons.vehicles.java.optionals.dto;

import java.math.BigDecimal;

public record OptionalDTOtoQuoted(
        Integer id,
        String vehicleTypeIt,
        String vehicleTypeEn,
        String nameIt,
        String nameEn,
        BigDecimal price) {
}
