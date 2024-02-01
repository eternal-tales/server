package com.eternaltales.eternaltalesserver.domain.pet.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
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

import com.eternaltales.eternaltalesserver.domain.member.entity.Member;
import com.eternaltales.eternaltalesserver.domain.member.repository.MemberRepository;
import com.eternaltales.eternaltalesserver.domain.pet.dto.GetPetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetInfoDtoReq;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PostPetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PutPetCardReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.entity.Pet;
import com.eternaltales.eternaltalesserver.domain.pet.repository.PetRepository;
import com.eternaltales.eternaltalesserver.domain.pet.service.PetService;
import com.eternaltales.eternaltalesserver.domain.pet.vo.PetType;
import com.eternaltales.eternaltalesserver.domain.pethistory.entity.PetHistory;
import com.eternaltales.eternaltalesserver.domain.pethistory.repository.PetHistoryRepository;
import com.eternaltales.eternaltalesserver.global.exception.BaseException;
import com.eternaltales.eternaltalesserver.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/pets")
@Tag(name = "pets 관련 API", description = "pets 관련 API")
public class PetController {
	private final PetService petService;

	private Long memberId = 1L;

	@PostMapping("/info")
	public BaseResponse<String> findPetInfo(
		@RequestPart(value = "petInfo") @Valid PetInfoDtoReq petInfoDtoReq,
		@RequestPart(value = "petImage") MultipartFile petImage
	) throws Exception {

		petService.postPetInfo(memberId,petInfoDtoReq, petImage);
		return new BaseResponse<>("펫 등록 했습니다.", HttpStatus.CREATED,"펫 등록 했습니다.");
	}
	@PutMapping("/info")
	public String putPetInfo(
		@RequestPart(value = "petInfo") @Valid PetInfoDtoReq petInfoDtoReq,
		@RequestPart(value = "petImage", required = false) MultipartFile petImage
	) {
		return "info";
	}

	@PostMapping("/{petId}/history")
	public BaseResponse<String> postPetHistory(
		@PathVariable(name = "petId") Long petId,
		@RequestBody PostPetHistoryReqDto postPetHistoryReqDto
	) throws Exception {

		petService.postPetHistory(petId,postPetHistoryReqDto);

		return new BaseResponse<>("대화 잘 전달 됐습니다.", HttpStatus.CREATED,"대화 잘 전달 됐습니다.");
	}

	@PutMapping("/{petId}/ended")
	public BaseResponse<Boolean> checkPetEnded(
		@PathVariable(name = "petId") Long petId
	) {


		if (petService.putPetEnded(petId)){
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

	@GetMapping("/{petId}/hisotry")
	public BaseResponse<List<GetPetHistoryReqDto>> findPetHistoryList(
		@PathVariable(name = "petId") Long petId
	) {
		List<GetPetHistoryReqDto> getPetHistoryReqDto = petService.getPetHistoryList(petId);
		return new BaseResponse<>(getPetHistoryReqDto, HttpStatus.OK,"펫 리스트 가져왔습니다.");
	}

	@DeleteMapping("/{petId}/hisotry/{petHistoryId}")
	public BaseResponse<String> deletePetHistory(
		@PathVariable(name = "petId") Long petId,
		@PathVariable(name = "petHistoryId") Long petHistoryId
	) {
		petService.deletePetHistory(petHistoryId, petId);
		return new BaseResponse<>("해당 기록을 지웠습니다.", HttpStatus.OK,"해당 기록을 지웠습니다.");
	}

	@PostMapping("/{petId}/hisotry/comment")
	public String postPetHistoryComment(
		@PathVariable(name = "petId") Long petId
	) {
		return "info";
	}
}
