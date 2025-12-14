package org.lessons.vehicles.java.quoted.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.lessons.vehicles.java.optionals.dto.OptionalDTOtoQuoted;
import org.lessons.vehicles.java.optionals.model.Optionals;
import org.lessons.vehicles.java.optionals.repository.OptionalsRepository;
import org.lessons.vehicles.java.quoted.dto.PriceAdjustment; // <--- FONDAMENTALE PER IL PDF
import org.lessons.vehicles.java.quoted.dto.QuotedDTO;
import org.lessons.vehicles.java.quoted.model.Quoted;
import org.lessons.vehicles.java.quoted.repository.QuotedRepository;
import org.lessons.vehicles.java.user.model.User;
import org.lessons.vehicles.java.user.repository.UserRepository;
import org.lessons.vehicles.java.vehicle.dto.VehicleDTOToQuoted;
import org.lessons.vehicles.java.vehicle.model.Vehicle;
import org.lessons.vehicles.java.vehicle.repository.VehicleRepository;
import org.lessons.vehicles.java.vehicleVariation.dto.VehicleVariationDTO;
import org.lessons.vehicles.java.vehicleVariation.model.VehicleVariation;
import org.lessons.vehicles.java.vehicleVariation.repository.VehicleVariationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuotedService {

    private final QuotedRepository quotedRepository;
    private final PriceCalculatorService priceCalculatorService;
    private final VehicleRepository vehicleRepository;
    private final OptionalsRepository optionalsRepository;
    private final VehicleVariationRepository vehicleVariationRepository;
    private final UserRepository userRepository;

    public QuotedService(QuotedRepository quotedRepository,
            PriceCalculatorService priceCalculatorService,
            VehicleRepository vehicleRepository,
            OptionalsRepository optionalsRepository,
            VehicleVariationRepository vehicleVariationRepository,
            UserRepository userRepository) {
        this.quotedRepository = quotedRepository;
        this.priceCalculatorService = priceCalculatorService;
        this.vehicleRepository = vehicleRepository;
        this.optionalsRepository = optionalsRepository;
        this.vehicleVariationRepository = vehicleVariationRepository;
        this.userRepository = userRepository;
    }

    public List<QuotedDTO> getAllQuoted() {
        return quotedRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<QuotedDTO> getQuotedByUserMail(String email) {
        List<Quoted> quotedEntities = quotedRepository.findByUserMail(email);

        if (quotedEntities.isEmpty()) {
            return List.of();
        }

        return quotedEntities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- METODI DI CALCOLO (AGGIORNATI PER PDF) ---

    /**
     * Calcola il prezzo finale e popola la lista adjustments per il PDF.
     */
    private BigDecimal calculateFinalPrice(Quoted quoted, List<PriceAdjustment> adjustments) {
        BigDecimal total = BigDecimal.ZERO;

        Vehicle v = quoted.getVehicle();
        VehicleVariation selectedVariation = quoted.getVehicleVariation();

        // 1. Prezzo Veicolo Base + Variazioni (Delega al PriceCalculatorService)
        if (v != null) {
            VehicleVariationDTO varDTO = selectedVariation != null ? toVariationDTO(selectedVariation) : null;
            // Nota: Assicurati che PriceCalculatorService accetti la lista come terzo parametro!
            // Se PriceCalculatorService è stato revertato, potrebbe dare errore qui.
            // Nel caso, fammelo sapere.
            total = priceCalculatorService.calculateVehiclePrice(v, varDTO, adjustments);
        }

        // 2. Aggiunta Optionals (Semplice somma)
        if (quoted.getOptionals() != null) {
            for (Optionals o : quoted.getOptionals()) {
                if (o.getPrice() != null) {
                    total = total.add(o.getPrice());
                }
            }
        }

        // --- APPLICAZIONE SCONTI E REGOLE (CON TRACCIAMENTO) ---

        // Regola: Sconto se ci sono 3 o più optional
        int optionalCount = quoted.getOptionals() != null ? quoted.getOptionals().size() : 0;
        if (optionalCount >= 3) {
            BigDecimal discountedTotal = total.multiply(BigDecimal.valueOf(0.97));
            BigDecimal discountAmount = discountedTotal.subtract(total); // Sarà negativo
            adjustments.add(new PriceAdjustment("Sconto Pacchetto Optionals (3+)", discountAmount));
            total = discountedTotal;
        }

        // Regola: Sconto Immatricolazione anno corrente
        if (selectedVariation != null
                && selectedVariation.getImmatricolationYear() != null
                && selectedVariation.getImmatricolationYear() == Year.now().getValue()) {
            BigDecimal discountedTotal = total.multiply(BigDecimal.valueOf(0.98));
            BigDecimal discountAmount = discountedTotal.subtract(total);
            adjustments.add(new PriceAdjustment("Promo Immatricolazione Anno Corrente", discountAmount));
            total = discountedTotal;
        }

        // Regola: Luxury Tax / Sconto sopra i 20k
        if (total.compareTo(BigDecimal.valueOf(20000)) > 0) {
            BigDecimal excess = total.subtract(BigDecimal.valueOf(20000));
            // Sconto del 5% sull'eccedenza
            BigDecimal discountAmount = excess.multiply(BigDecimal.valueOf(0.05)).negate();
            adjustments.add(new PriceAdjustment("Sconto su importo eccedente €20.000", discountAmount));
            total = total.add(discountAmount);
        }

        // Regola: Sconto Pacchetto Comfort (Clima + Nav)
        boolean hasClimatizzatore = quoted.getOptionals() != null
                && quoted.getOptionals().stream()
                        .anyMatch(o -> "climatizzatore".equalsIgnoreCase(o.getNameIt())
                                || "air conditioning".equalsIgnoreCase(o.getNameEn()));
        boolean hasNavigatore = quoted.getOptionals() != null
                && quoted.getOptionals().stream()
                        .anyMatch(o -> "navigatore".equalsIgnoreCase(o.getNameIt())
                                || "navigator".equalsIgnoreCase(o.getNameEn()));

        if (hasClimatizzatore && hasNavigatore) {
            BigDecimal discountAmount = BigDecimal.valueOf(-100);
            adjustments.add(new PriceAdjustment("Promo Pacchetto Comfort (Clima + Nav)", discountAmount));
            total = total.add(discountAmount);
        }

        return total;
    }

    public QuotedDTO toDTO(Quoted quoted) {
        if (quoted == null) {
            return null;
        }
        
        // 1. Preparo la lista per il PDF
        List<PriceAdjustment> adjustments = new ArrayList<>();

        // 2. Calcolo il prezzo riempiendo la lista
        BigDecimal finalPrice = calculateFinalPrice(quoted, adjustments);

        Integer id = quoted.getId();
        Integer userId = quoted.getUser() != null ? quoted.getUser().getId() : null;
        String userName = quoted.getUser() != null ? quoted.getUser().getName() : null;
        String userSurname = quoted.getUser() != null ? quoted.getUser().getSurname() : null;
        String userMail = quoted.getUser() != null ? quoted.getUser().getMail() : null;
        String userEmail = quoted.getUser() != null ? quoted.getUser().getEmail() : null;

        List<VehicleDTOToQuoted> vehicles = List.of();

        if (quoted.getVehicle() != null) {
            Vehicle vehicle = quoted.getVehicle();
            List<VehicleVariationDTO> variationList = List.of();
            if (quoted.getVehicleVariation() != null) {
                variationList = List.of(toVariationDTO(quoted.getVehicleVariation()));
            }

            VehicleDTOToQuoted vehicleDTO = new VehicleDTOToQuoted(
                    vehicle.getId(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getBasePrice(),
                    variationList);

            vehicles = List.of(vehicleDTO);
        }

        Integer vehicleVariationId = quoted.getVehicleVariation() != null
                ? quoted.getVehicleVariation().getId()
                : null;

        List<OptionalDTOtoQuoted> optionals = quoted.getOptionals() != null
                ? quoted.getOptionals().stream()
                        .map(this::toOptionalDTO)
                        .collect(Collectors.toList())
                : List.of();

        // 3. Creo il DTO passando anche adjustments alla fine
        return new QuotedDTO(id, userId, userName, userSurname, userMail, userEmail, vehicles, vehicleVariationId,
                optionals,
                finalPrice, 
                adjustments); // <--- L'ULTIMO PEZZO MANCANTE
    }

    // --- METODI DI CREAZIONE/AGGIORNAMENTO ---

    public QuotedDTO createQuoted(QuotedDTO quotedDTO) {
        Quoted newQuoted = toEntity(quotedDTO);

        // Uso una lista temporanea perché qui serve solo calcolare il totale per il DB
        List<PriceAdjustment> tempAdjustments = new ArrayList<>();
        newQuoted.setFinalPrice(calculateFinalPrice(newQuoted, tempAdjustments));

        Quoted savedQuoted = quotedRepository.save(newQuoted);

        return toDTO(savedQuoted);
    }

    public QuotedDTO updateQuoted(Integer id, QuotedDTO quotedDTO) {
        Quoted existingQuoted = quotedRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Quotation not found with id: " + id));

        if (quotedDTO.userId() != null) {
            User user = userRepository.findById(quotedDTO.userId())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setName(quotedDTO.userName() != null ? quotedDTO.userName() : "default");
                        newUser.setSurname(quotedDTO.userSurname() != null ? quotedDTO.userSurname() : "default");
                        newUser.setMail(quotedDTO.userMail() != null ? quotedDTO.userMail() : "default@email.com");
                        newUser.setEmail(quotedDTO.userEmail() != null ? quotedDTO.userEmail() : "default@email.com");
                        newUser.setPassword("temporary");
                        newUser.setIsFirstQuotation(true);
                        return userRepository.save(newUser);
                    });
            existingQuoted.setUser(user);
        }

        if (existingQuoted.getOptionals() == null) {
            existingQuoted.setOptionals(new ArrayList<>());
        }

        if (quotedDTO.vehicleDTOToQuoted() != null && !quotedDTO.vehicleDTOToQuoted().isEmpty()) {
            VehicleDTOToQuoted vDTO = quotedDTO.vehicleDTOToQuoted().get(0);
            Vehicle vehicle = vehicleRepository.findById(vDTO.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Vehicle not found with id: " + vDTO.id()));
            existingQuoted.setVehicle(vehicle);
        }

        if (quotedDTO.vehicleVariationId() != null) {
            VehicleVariation variation = vehicleVariationRepository.findById(quotedDTO.vehicleVariationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Vehicle variation not found with id: " + quotedDTO.vehicleVariationId()));
            existingQuoted.setVehicleVariation(variation);
        }

        if (quotedDTO.optionalDTOtoQuoted() != null) { // Nota: ho tolto il controllo isEmpty per permettere di svuotare gli optional
            List<Optionals> optionals = new ArrayList<>();
            for (OptionalDTOtoQuoted oDTO : quotedDTO.optionalDTOtoQuoted()) {
                Optionals optional = optionalsRepository.findById(oDTO.id())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Optional not found with id: " + oDTO.id()));
                optionals.add(optional);
            }
            existingQuoted.setOptionals(optionals);
        }

        // Ricalcolo prezzo e salvo
        List<PriceAdjustment> tempAdjustments = new ArrayList<>();
        existingQuoted.setFinalPrice(calculateFinalPrice(existingQuoted, tempAdjustments));

        Quoted savedQuoted = quotedRepository.save(existingQuoted);

        return toDTO(savedQuoted);
    }

    public QuotedDTO getQuotedById(Integer id) {
        Quoted quoted = quotedRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Quotation not found with id: " + id));
        return toDTO(quoted);
    }

    // --- HELPER DI MAPPING ---

    private VehicleVariationDTO toVariationDTO(VehicleVariation variation) {
        if (variation == null) return null;
        return new VehicleVariationDTO(
                variation.getId(),
                variation.getCc(),
                variation.getImmatricolationMonth(),
                variation.getImmatricolationYear(),
                variation.getFuelSystemIt(),
                variation.getFuelSystemEn());
    }

    private OptionalDTOtoQuoted toOptionalDTO(Optionals optional) {
        if (optional == null) return null;

        // L'errore era qui: stavi calcolando 'finalName' e passavi solo 3 dati.
        // Invece devi passare TUTTI e 6 i dati che il tuo DTO si aspetta.
        
        return new OptionalDTOtoQuoted(
                optional.getId(),             // 1. ID
                optional.getVehicleTypeIt(),  // 2. Tipo Veicolo IT
                optional.getVehicleTypeEn(),  // 3. Tipo Veicolo EN
                optional.getNameIt(),         // 4. Nome IT (es. "Bauletto")
                optional.getNameEn(),         // 5. Nome EN
                optional.getPrice()           // 6. Prezzo
        );
    }
    
    private Quoted toEntity(QuotedDTO quotedDTO) {
        Quoted quoted = new Quoted();

        if (quotedDTO.userMail() != null || quotedDTO.userName() != null) {
            User user = new User();
            user.setName(quotedDTO.userName() != null ? quotedDTO.userName() : "default");
            user.setSurname(quotedDTO.userSurname() != null ? quotedDTO.userSurname() : "default");
            user.setMail(quotedDTO.userMail() != null ? quotedDTO.userMail() : "noemail-" + UUID.randomUUID());
            user.setEmail(quotedDTO.userEmail() != null ? quotedDTO.userEmail() : "noemail-" + UUID.randomUUID());
            user.setPassword("temporary");
            user.setIsFirstQuotation(true);

            user = userRepository.save(user);
            quoted.setUser(user);
        }

        if (quotedDTO.vehicleDTOToQuoted() != null && !quotedDTO.vehicleDTOToQuoted().isEmpty()) {
            VehicleDTOToQuoted vDTO = quotedDTO.vehicleDTOToQuoted().get(0);
            Vehicle vehicle = vehicleRepository.findById(vDTO.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Vehicle not found with id: " + vDTO.id()));
            quoted.setVehicle(vehicle);
        }

        if (quotedDTO.vehicleVariationId() != null) {
            VehicleVariation variation = vehicleVariationRepository.findById(quotedDTO.vehicleVariationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Vehicle variation not found with id: " + quotedDTO.vehicleVariationId()));
            quoted.setVehicleVariation(variation);
        }

        if (quotedDTO.optionalDTOtoQuoted() != null && !quotedDTO.optionalDTOtoQuoted().isEmpty()) {
            List<Optionals> optionals = new ArrayList<>();
            for (OptionalDTOtoQuoted oDTO : quotedDTO.optionalDTOtoQuoted()) {
                Optionals optional = optionalsRepository.findById(oDTO.id())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Optional not found with id: " + oDTO.id()));
                optionals.add(optional);
            }
            quoted.setOptionals(optionals);
        }

        return quoted;
    }
}