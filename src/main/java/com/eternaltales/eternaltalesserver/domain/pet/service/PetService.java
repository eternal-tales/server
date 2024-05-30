package com.eternaltales.eternaltalesserver.domain.pet.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.eternaltales.eternaltalesserver.domain.member.entity.Member;
import com.eternaltales.eternaltalesserver.domain.member.repository.MemberRepository;
import com.eternaltales.eternaltalesserver.domain.pet.dto.AiPostPetChatResDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.GetPetGalleryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetInfoDtoReq;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PetResData;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PostPetHistoryReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.dto.PutPetCardReqDto;
import com.eternaltales.eternaltalesserver.domain.pet.entity.Pet;
import com.eternaltales.eternaltalesserver.domain.pet.repository.PetRepository;
import com.eternaltales.eternaltalesserver.domain.pet.vo.MediaType;
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
	public Long postPetInfo(Long memberId, PetInfoDtoReq petInfoDtoReq, MultipartFile image) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			()->new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 유저 id는 없습니다."));

		try{
			Pet pet = Pet.builder()
				.petType(PetType.valueOf(petInfoDtoReq.getPetType()))
				.feature(petInfoDtoReq.getFeature())
				.name(petInfoDtoReq.getName())
				.member(member)
				.build();

			petRepository.save(pet);

			String imageKey = "assets/eternaltales/images/pets/"+pet.getId()+"/" + UUID.randomUUID() + ".png";
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(imageKey)
				.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
			String imageUrl = storageUrl + imageKey;

			pet.setImageUrl(imageUrl);

			return pet.getId();
		}
		catch (Exception e){
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버상에서 오류가 났습니다.");
		}

	}

	@Transactional
	public List<PetHistoryReqDto> postPetHistory(Long memberId, Long petId, PostPetHistoryReqDto postPetHistoryReqDto) {
		List<PetHistoryReqDto> petHistoryReqDtos = new ArrayList<>();
		PetHistory petHistoryPerson = PetHistory.builder()
			.pet(petRepository.findById(petId).orElseThrow(
				() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 id는 없습니다.")
			))
			.content(postPetHistoryReqDto.getContent())
			.objectType(ObjectType.PERSON)
			.mediaType(MediaType.TEXT)
			.wished(false)
			.name(memberRepository.findById(memberId).orElseThrow(
				() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 유저 id는 없습니다.")
			).getName())
			.build();
		PetHistory newPetHistory = petHistoryRepository.save(petHistoryPerson);
		petHistoryReqDtos.add(PetHistoryReqDto.builder()
			.mediaType(newPetHistory.getMediaType())
			.objectType(newPetHistory.getObjectType())
			.petHistoryId(newPetHistory.getId())
			.wished(newPetHistory.getWished())
			.content(newPetHistory.getContent())
			.name(newPetHistory.getName())
			.createdAt(newPetHistory.getCreatedAt())
			.build());

		// FastAPI 연동 코드
		String baseUrl = "http://localhost:8088/api/v1/pets/chat";
		boolean hasImage = false;

		if (getPetHistoryList(petId).size()%10 == 0){
			hasImage = true;
		}
		String contentUrl = "?content=" + postPetHistoryReqDto.getContent() + "&hasImage=" + hasImage;
		String requestUrl = baseUrl + contentUrl;

		RestTemplate restTemplate = new RestTemplate();

		AiPostPetChatResDto response = restTemplate.postForEntity(requestUrl,null, AiPostPetChatResDto.class).getBody();

		assert response != null;
		List<PetResData> petResDataList = response.getPetResDataList();

		try{
			for (int i=0; i < petResDataList.size(); i++ ){
				Pet pet = petRepository.findById(petId).orElseThrow(
					() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 id는 없습니다.")
				);
				PetHistory petHistoryPet = PetHistory.builder()
					.pet(pet)
					.content(petResDataList.get(i).getContent())
					.objectType(ObjectType.PET)
					.mediaType(MediaType.valueOf(petResDataList.get(i).getMediaType()))
					.name(pet.getName())
					.wished(false)
					.build();
				petHistoryRepository.save(petHistoryPet);

				petHistoryReqDtos.add(PetHistoryReqDto.builder()
					.mediaType(petHistoryPet.getMediaType())
					.objectType(petHistoryPet.getObjectType())
					.petHistoryId(petHistoryPet.getId())
					.wished(petHistoryPet.getWished())
					.content(petHistoryPet.getContent())
					.name(petHistoryPet.getName())
					.createdAt(petHistoryPet.getCreatedAt())
					.build());

			}
			return petHistoryReqDtos;
		}
		catch (Exception e){
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.value(),"서버 오류");
		}
	}

	@Transactional
	public boolean getPetEnded(Long petId) {
		Pet pet = petRepository.findById(petId).orElseThrow(
			() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 id는 없습니다.")
		);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime petEndedDateTime = pet.getCreatedAt().plusDays(49);

		return now.isAfter(petEndedDateTime);
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
	public List<PetHistoryReqDto> getPetHistoryList(Long petId){
		return petHistoryRepository.findAllByPet_IdAndPet_DeletedAtIsNullAndDeletedAtIsNullOrderByCreatedAtAsc(petId).stream().map(
			petHistory -> PetHistoryReqDto.builder()
				.petHistoryId(petHistory.getId())
				.content(petHistory.getContent())
				.name(petHistory.getName())
				.mediaType(petHistory.getMediaType())
				.objectType(petHistory.getObjectType())
				.wished(petHistory.getWished())
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

	@Transactional
	public Boolean putPetHistoryWished(Long petHistoryId, Long petId){
		PetHistory petHistory = petHistoryRepository.findByIdAndPet_Id(petHistoryId, petId).orElseThrow(
			() -> new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "해당 기록은 없습니다."));

		if (petHistory.getMediaType() == MediaType.IMAGE) {
			if (petHistoryRepository.findAllByPet_IdAndWishedIsTrueOrderByCreatedAtAsc(petId).size() < 6){
				petHistory.setWished(!petHistory.getWished());

				return petHistory.getWished();
			}
			else{
				throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "이미 3개 다 골랐습니다.");
			}
		}
		else{
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "해당 media는 텍스트 입니다.");
		}
	}

	@Transactional
	public Boolean putPetLeave(Long petId, boolean isPetLeave){
		Pet pet = petRepository.findById(petId).orElseThrow(
			() -> new BaseException(HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value(),"해당 펫 id는 없습니다.")
		);

		if (isPetLeave){
			pet.setDeletedAt(LocalDateTime.now());
			return true;
		}
		else{
			return false;
		}
	}

	@Transactional
	public List<GetPetGalleryReqDto> getPetGallery(Long petId){
		List<PetHistory> petHistoryRepositories = petHistoryRepository.findAllByPet_IdAndWishedIsTrueOrderByCreatedAtAsc(petId);

		List<GetPetGalleryReqDto> getPetGalleryReqDtos = new ArrayList<>();
		for (int i = 0; i< petHistoryRepositories.size() /2; i++) {
			getPetGalleryReqDtos.add(GetPetGalleryReqDto
				.builder()
					.name(petHistoryRepositories.get(i*2).getName())
					.petHistoryId(petHistoryRepositories.get(i*2).getId())
					.imageUrl(petHistoryRepositories.get(i*2).getContent())
					.message(petHistoryRepositories.get(i*2+1).getContent())
					.createdAt(petHistoryRepositories.get(i*2+1).getCreatedAt())
				.build());
		}
		return getPetGalleryReqDtos;
	}
}
