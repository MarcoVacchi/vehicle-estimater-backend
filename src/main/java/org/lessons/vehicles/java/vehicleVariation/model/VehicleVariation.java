package org.lessons.vehicles.java.vehicleVariation.model;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vehicleVariation")
public class VehicleVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "The capacity must be declared, and must be min 1")
    @Min(value = 1, message = "The capacity must be 1 or more")
    private Integer cc;

    @NotBlank(message = "This field cannot be blank, null or empty, and must be min 3 char and max 100 char")
    @Size(min = 3, max = 100)
    @Column(name = "immatricolation_month", nullable = false)
    private Date immatricolationMonth;

    @NotNull(message = "The capacity must be declared, and must be min 1")
    @Size(min = 3, max = 100)
    @Column(name = "immatricolation_year", nullable = false)
    private Integer immatricolationYear;

    // sara un array
    @NotBlank(message = "This field cannot be blank, null or empty, and must be min 3 char and max 100 char")
    @Size(min = 3, max = 100)
    @Column(name = "fuel_system", nullable = false)
    private String fuelSystem;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCc() {
        return this.cc;
    }

    public void setCc(Integer cc) {
        this.cc = cc;
    }

    public Date getImmatricolationMonth() {
        return this.immatricolationMonth;
    }

    public void setImmatricolationMonth(Date immatricolationMonth) {
        this.immatricolationMonth = immatricolationMonth;
    }

    public Integer getImmatricolationYear() {
        return this.immatricolationYear;
    }

    public void setImmatricolationYear(Integer immatricolationYear) {
        this.immatricolationYear = immatricolationYear;
    }

    public String getFuelSystem() {
        return this.fuelSystem;
    }

    public void setFuelSystem(String fuelSystem) {
        this.fuelSystem = fuelSystem;
    }

}