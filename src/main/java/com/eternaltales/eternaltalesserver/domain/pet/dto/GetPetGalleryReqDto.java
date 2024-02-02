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
public class GetPetGalleryReqDto {
	private Long petHistoryId;

	private String name;

	private String imageUrl;

	private String message;

	private LocalDateTime createdAt;
}
