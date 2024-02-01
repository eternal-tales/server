package com.eternaltales.eternaltalesserver.domain.pet.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetPetHistoryReqDto {
	private Long petHistoryId;

	private String content;

	private LocalDateTime createdAt;
}
