package org.lessons.vehicles.java.quoted.dto;

import java.math.BigDecimal;
import java.util.List;

import org.lessons.vehicles.java.optionals.dto.OptionalDTOtoQuoted;
import org.lessons.vehicles.java.vehicle.dto.VehicleDTOToQuoted;

public record QuotedDTO(
        Integer id,
        Integer userId,
        String userName,
        String userSurname,
        String userMail,
        String userEmail, // <--- Nota: Vedo dai log che hai due campi email ora (mail e email)
        List<VehicleDTOToQuoted> vehicleDTOToQuoted,
        Integer vehicleVariationId,
        List<OptionalDTOtoQuoted> optionalDTOtoQuoted,
        BigDecimal finalPrice,
        
        // IL CAMPO MANCANTE ERA QUESTO:
        List<PriceAdjustment> priceAdjustments 
) {}