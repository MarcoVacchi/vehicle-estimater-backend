package org.lessons.vehicles.java.quoted.model;

import org.lessons.vehicles.java.vehicle.model.Vehicle;

import jakarta.persistence.*;

@Entity
@Table(name = "quoted")
public class Quoted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}