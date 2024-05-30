package com.eternaltales.eternaltalesserver.domain.pet.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AiPostPetChatResDto {
	private List<PetResData> petResDataList;
	private String msg;
}
