package com.eternaltales.eternaltalesserver.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eternaltales.eternaltalesserver.domain.pet.entity.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}