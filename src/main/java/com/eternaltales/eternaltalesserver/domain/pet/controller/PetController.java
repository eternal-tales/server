package com.eternaltales.eternaltalesserver.domain.pet.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eternaltales.eternaltalesserver.domain.pet.dto.PetInfoDtoReq;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/pets")
@Tag(name = "pets 관련 API", description = "pets 관련 API")
public class PetController {
	@PutMapping("/info")
	public String putPetInfo(
		@RequestBody PetInfoDtoReq petInfoDtoReq
	) {
		return "info";
	}

	@PostMapping("/{petId}/history")
	public String postPetHistory(
		@PathVariable(name = "petId") Long petId
	) {
		return "info";
	}

	@PutMapping("/{petId}/ended")
	public String putPetEnded(
		@PathVariable(name = "petId") Long petId
	) {
		return "info";
	}

	@PutMapping("/{petId}/end-card")
	public String putPetEndCard(
		@PathVariable(name = "petId") Long petId
	) {
		return "info";
	}

	@GetMapping("/{petId}/hisotry")
	public String getPetHistory(
		@PathVariable(name = "petId") Long petId
	) {
		return "info";
	}

	@DeleteMapping("/{petId}/hisotry/{hisotryId}")
	public String deletePetHistory(
		@PathVariable(name = "petId") Long petId,
		@PathVariable(name = "historyId") Long hisotryId
	) {
		return "info";
	}

	@PostMapping("/{petId}/hisotry/comment")
	public String postPetHistoryComment(
		@PathVariable(name = "petId") Long petId
	) {
		return "info";
	}
}
