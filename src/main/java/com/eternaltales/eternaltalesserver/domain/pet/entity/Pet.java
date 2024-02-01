package com.eternaltales.eternaltalesserver.domain.pet.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.eternaltales.eternaltalesserver.domain.member.entity.Member;
import com.eternaltales.eternaltalesserver.domain.pet.vo.PetType;
import com.eternaltales.eternaltalesserver.domain.pethistory.entity.PetHistory;
import com.eternaltales.eternaltalesserver.global.mixin.TimestampMixin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Pet extends TimestampMixin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false, length = 1024, unique = true, updatable = false)
	private String username;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(length = 2048, name = "image_url")
	private String imageUrl;

	@Column(nullable = false, length = 2024, name = "pet_type")
	@Enumerated(EnumType.STRING)
	private PetType petType;

	@Column(length = 1024)
	private String featrue;

	@OneToMany(mappedBy = "pet")
	private List<PetHistory> petHistories = new ArrayList<>();
}
