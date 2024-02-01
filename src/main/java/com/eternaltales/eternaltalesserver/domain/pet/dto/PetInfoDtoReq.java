package com.eternaltales.eternaltalesserver.domain.pet.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetInfoDtoReq {
	@NotBlank
	private String petType;

	@NotBlank
	private String name;

	@NotBlank
	private String feature;


}
