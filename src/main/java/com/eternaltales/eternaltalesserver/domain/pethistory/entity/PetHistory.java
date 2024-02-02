package com.eternaltales.eternaltalesserver.domain.pethistory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.eternaltales.eternaltalesserver.domain.pet.entity.Pet;
import com.eternaltales.eternaltalesserver.domain.pet.vo.MediaType;
import com.eternaltales.eternaltalesserver.domain.pethistory.vo.ObjectType;
import com.eternaltales.eternaltalesserver.global.mixin.TimestampMixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet_history")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PetHistory extends TimestampMixin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;

	@Column(nullable = false, length = 2048)
	private String name;

	@Column(nullable = false, length = 2048)
	private String content;

	@Column(nullable = false, length = 2024, name = "object_type")
	@Enumerated(EnumType.STRING)
	private ObjectType objectType;

	@Column(nullable = false, length = 2024, name = "media_type")
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;

	@ColumnDefault("false")
	private Boolean wished;
}
