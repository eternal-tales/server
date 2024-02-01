package com.eternaltales.eternaltalesserver.domain.pet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.eternaltales.eternaltalesserver.domain.member.entity.Member;
import com.eternaltales.eternaltalesserver.domain.member.repository.MemberRepository;
import com.eternaltales.eternaltalesserver.domain.pet.dto.AiPostPetChatResDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.GetPetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetInfoDtoReq;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PostPetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PutPetCardReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.entity.Pet;
import com.eternaltales.eternaltalesserver.domain.pet.repository.PetRepository;
import com.eternaltales.eternaltalesserver.domain.pet.vo.PetType;
import com.eternaltales.eternaltalesserver.domain.pethistory.entity.PetHistory;
import com.eternaltales.eternaltalesserver.domain.pethistory.repository.PetHistoryRepository;
import com.eternaltales.eternaltalesserver.domain.pethistory.vo.ObjectType;
import com.eternaltales.eternaltalesserver.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {
	private final MemberRepository memberRepository;
	private final PetRepository petRepository;
	private final PetHistoryRepository petHistoryRepository;
	private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${eternal-tales.services.storage.url}")
	private String storageUrl;

	@Transactional
	public void postPetInfo(Long memberId, PetInfoDtoReq petInfoDtoReq, MultipartFile image) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			()->new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 유저 id는 없습니다."));
		String imageKey = "assets/eternaltales/images/pets" + UUID.randomUUID() + ".png";
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(imageKey)
			.build();

		try{
			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
			String imageUrl = storageUrl + imageKey;
			Pet pet = Pet.builder()
				.petType(PetType.valueOf(petInfoDtoReq.getPetType()))
				.feature(petInfoDtoReq.getFeature())
				.name(petInfoDtoReq.getName())
				.member(member)
				.imageUrl(imageUrl)
				.build();

			System.out.println("앗");
			petRepository.save(pet);
		}
		catch (Exception e){
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버상에서 오류가 났습니다.");
		}

	}

	@Transactional
	public void postPetHistory(Long petId, PostPetHistoryReqDto postPetHistoryReqDto) {
		PetHistory petHistoryPerson = PetHistory.builder()
			.pet(petRepository.findById(petId).orElseThrow(
				() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 id는 없습니다.")
			))
			.content(postPetHistoryReqDto.getContent())
			.objectType(ObjectType.PERSON)
			.build();
		petHistoryRepository.save(petHistoryPerson);

		// FastAPI 연동 코드
		String baseUrl = "http://localhost:8088/api/v1/pets/chat";
		String contentUrl = "?content=" + postPetHistoryReqDto.getContent();
		String requestUrl = baseUrl + contentUrl;

		RestTemplate restTemplate = new RestTemplate();

		AiPostPetChatResDto response = restTemplate.postForEntity(requestUrl,null, AiPostPetChatResDto.class).getBody();

		try{
			PetHistory petHistoryPet = PetHistory.builder()
				.pet(petRepository.findById(petId).orElseThrow(
					() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 id는 없습니다.")
				))
				.content(response.getData())
				.objectType(ObjectType.PET)
				.build();

			petHistoryRepository.save(petHistoryPet);
		}
		catch (Exception e){
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.value(),"서버 오류");
		}


	}

	@Transactional
	public boolean putPetEnded(Long petId) {
		Pet pet = petRepository.findById(petId).orElseThrow(
			() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 id는 없습니다.")
		);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime petEndedDateTime = pet.getCreatedAt().plusDays(49);

		if (now.isAfter(petEndedDateTime)){

			pet.setDeletedAt(now);
			return true;
		}
		else{
			return false;
		}
	}

	@Transactional
	public void putPetEndCard(PutPetCardReqDto putPetCardReqDto, Long petId){
		putPetCardReqDto.getPetHistoryImages().stream().map(petHistoryId -> {
			PetHistory petHistory = petHistoryRepository.findByIdAndPet_Id(petId, petHistoryId).orElseThrow(
				()-> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 기록은 없습니다."));
			petHistory.setDeletedAt(LocalDateTime.now());

			return null;
		});
	}


	@Transactional
	public List<GetPetHistoryReqDto> getPetHistoryList(Long petId){
		System.out.println(petId);
		return petHistoryRepository.findAllByPet_IdAndDeletedAtIsNullOrderByCreatedAtDesc(petId).stream().map(
			petHistory -> GetPetHistoryReqDto.builder()
				.petHistoryId(petHistory.getId())
				.content(petHistory.getContent())
				.createdAt(petHistory.getCreatedAt())
				.build()
		).toList();
	}

	@Transactional
	public void deletePetHistory(Long petHistoryId, Long petId){
		PetHistory petHistory = petHistoryRepository.findByIdAndPet_Id(petHistoryId, petId).orElseThrow(
			() -> new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "해당 기록은 없습니다."));

		petHistory.setDeletedAt(LocalDateTime.now());
	}
}
