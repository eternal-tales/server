package com.eternaltales.eternaltalesserver.domain.pet.dto;

import java.util.List;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutPetCardReqDto {
	@Size(max = 3, message = "리스트는 최대 3개의 요소만 허용됩니다.")
	private List<Long> petHistoryImages;
}
