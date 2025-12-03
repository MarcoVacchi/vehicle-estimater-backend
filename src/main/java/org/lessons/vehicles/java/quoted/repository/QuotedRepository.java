package org.lessons.vehicles.java.quoted.repository;

import org.lessons.vehicles.java.quoted.model.Quoted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotedRepository extends JpaRepository<Quoted, Integer> {

}
