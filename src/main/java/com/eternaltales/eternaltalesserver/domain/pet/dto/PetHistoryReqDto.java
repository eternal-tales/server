package com.eternaltales.eternaltalesserver.domain.pet.dto;

import java.time.LocalDateTime;

import com.eternaltales.eternaltalesserver.domain.pet.vo.MediaType;
import com.eternaltales.eternaltalesserver.domain.pethistory.vo.ObjectType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PetHistoryReqDto {
	private Long petHistoryId;

	private String name;

	private String content;

	private LocalDateTime createdAt;

	private MediaType mediaType;

	private ObjectType objectType;

	private boolean wished;
}
