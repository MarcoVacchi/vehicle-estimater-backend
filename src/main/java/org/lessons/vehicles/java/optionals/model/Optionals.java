package org.lessons.vehicles.java.optionals.model;

import java.math.BigDecimal;

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
@Table(name = "optionals")
public class Optionals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "This field cannot be blank, null or empty, and must be  min 3 char and max 100 char")
    @Size(min = 3, max = 100)
    private String name;

    @NotNull(message = "The capacity must be declared, and must be min 1")
    @Min(value = 1, message = "The capacity must be 1 or more")
    private BigDecimal price;

    // sara un array
    @NotBlank(message = "This field cannot be blank, null or empty, and must be min 3 char and max 100 char")
    @Size(min = 3, max = 100)
    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

}
