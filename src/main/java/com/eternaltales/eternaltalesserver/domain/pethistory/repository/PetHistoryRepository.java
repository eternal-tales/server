package com.eternaltales.eternaltalesserver.domain.pethistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eternaltales.eternaltalesserver.domain.pethistory.entity.PetHistory;

@Repository
public interface PetHistoryRepository extends JpaRepository<PetHistory, Long> {
}
