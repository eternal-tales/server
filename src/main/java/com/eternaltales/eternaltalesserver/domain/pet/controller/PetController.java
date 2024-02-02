package com.eternaltales.eternaltalesserver.domain.pet.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eternaltales.eternaltalesserver.domain.pet.dto.GetPetGalleryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetInfoDtoReq;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetLeaveReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PostPetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PutPetCardReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.service.PetService;
import com.eternaltales.eternaltalesserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/pets")
@Tag(name = "pets 관련 API", description = "pets 관련 API")
public class PetController {
	private final PetService petService;

	private Long memberId = 1L;

	@PostMapping("/info")
	public BaseResponse<Long> findPetInfo(
		@RequestPart(value = "petInfo") @Valid PetInfoDtoReq petInfoDtoReq,
		@RequestPart(value = "petImage") MultipartFile petImage
	) throws Exception {

		Long petId = petService.postPetInfo(memberId,petInfoDtoReq, petImage);
		return new BaseResponse<>(petId, HttpStatus.CREATED,"펫 등록 했습니다.");
	}


	@PostMapping("/{petId}/history")
	public BaseResponse<List<PetHistoryReqDto>> postPetHistory(
		@PathVariable(name = "petId") Long petId,
		@RequestBody PostPetHistoryReqDto postPetHistoryReqDto
	) {

		List<PetHistoryReqDto> petHistoryReqDto = petService.postPetHistory(memberId, petId,postPetHistoryReqDto);

		return new BaseResponse<>(petHistoryReqDto, HttpStatus.CREATED,"대화 잘 전달 됐습니다.");
	}

	@GetMapping("/{petId}/ended")
	public BaseResponse<Boolean> checkPetEnded(
		@PathVariable(name = "petId") Long petId
	) {
		boolean isPetEnded = petService.getPetEnded(petId);
		if (isPetEnded){
			return new BaseResponse<>(true, HttpStatus.CREATED,"49일 경과 했습니다.");
		}
		else{
			return new BaseResponse<>(false, HttpStatus.CREATED,"49일 이전 입니다.");
		}
	}

	@PutMapping("/{petId}/end-card")
	public BaseResponse<String> putPetEndCard(
		@PathVariable(name = "petId") Long petId,
		@RequestBody @Valid PutPetCardReqDto putPetCardReqDto
	) {

		petService.putPetEndCard(putPetCardReqDto,petId);
		return new BaseResponse<>("3장 선택했습니다.", HttpStatus.CREATED,"3장 선택했습니다.");
	}

	@GetMapping("/{petId}/history")
	public BaseResponse<List<PetHistoryReqDto>> findPetHistoryList(
		@PathVariable(name = "petId") Long petId
	) {
		List<PetHistoryReqDto> PetHistoryReqDto = petService.getPetHistoryList(petId);
		return new BaseResponse<>(PetHistoryReqDto, HttpStatus.OK,"펫 리스트 가져왔습니다.");
	}

	@DeleteMapping("/{petId}/history/{petHistoryId}")
	public BaseResponse<String> deletePetHistory(
		@PathVariable(name = "petId") Long petId,
		@PathVariable(name = "petHistoryId") Long petHistoryId
	) {
		petService.deletePetHistory(petHistoryId, petId);
		return new BaseResponse<>("해당 기록을 지웠습니다.", HttpStatus.OK,"해당 기록을 지웠습니다.");
	}

	@PutMapping("/{petId}/history/{petHistoryId}/wished")
	public BaseResponse<Boolean> checkPetImageWished(
		@PathVariable(name = "petId") Long petId,
		@PathVariable(name = "petHistoryId") Long petHistoryId
	) {
		return new BaseResponse<>(petService.putPetHistoryWished(petHistoryId, petId), HttpStatus.OK,"찜 변경 했습니다.");
	}
	@PutMapping("/{petId}/leave")
	public BaseResponse<Boolean> putPetLeave(
		@PathVariable(name = "petId") Long petId,
		@RequestBody PetLeaveReqDto petLeaveReqDto
	) {
		boolean isPetLeave = petService.putPetLeave(petId, petLeaveReqDto.isPetLeave());
		return new BaseResponse<>(isPetLeave, HttpStatus.OK,"당신의 반료 동물을 삭제 했습니다.");
	}

	@GetMapping("/{petId}/gallery")
	public BaseResponse<List<GetPetGalleryReqDto>> findPetGallery(
		@PathVariable(name = "petId") Long petId
	) {
		List<GetPetGalleryReqDto> getPetGalleryReqDtos = petService.getPetGallery(petId);
		return new BaseResponse<>(getPetGalleryReqDtos, HttpStatus.OK,"기억 공간 가져왔습니다.");
	}

	// @PostMapping("/{petId}/hisotry/comment")
	// public String postPetHistoryComment(
	// 	@PathVariable(name = "petId") Long petId
	// ) {
	// 	return "info";
	// }
	//
	// @PutMapping("/info")
	// public String putPetInfo(
	// 	@RequestPart(value = "petInfo") @Valid PetInfoDtoReq petInfoDtoReq,
	// 	@RequestPart(value = "petImage", required = false) MultipartFile petImage
	// ) {
	// 	return "info";
	// }
}
