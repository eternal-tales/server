package com.eternaltales.eternaltalesserver.domain.pethistory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eternaltales.eternaltalesserver.domain.pethistory.entity.PetHistory;

@Repository
public interface PetHistoryRepository extends JpaRepository<PetHistory, Long> {

	Optional<PetHistory> findByIdAndPet_Id(long id, long petId);

	// @Query("SELECT pr FROM ProductReview pr "
	// 	+ "WHERE pr.user.id = :userId "
	// 	+ "AND pr.deletedAt IS NULL "
	// 	+ "AND pr.product.productCode = :productCode "
	// )
	// List<ProductReview> findFirstByUserIdAndProductCode(
	// 	@Param("userId") Long userId,
	// 	@Param("productCode") Long productCode
	// );

	List<PetHistory> findAllByPet_IdAndPet_DeletedAtIsNullAndDeletedAtIsNullOrderByCreatedAtAsc(Long id);

	List<PetHistory> findAllByPet_IdAndWishedIsTrueOrderByCreatedAtAsc(Long id);
}
