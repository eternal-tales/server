package com.eternaltales.eternaltalesserver.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.eternaltales.eternaltalesserver.domain.pet.entity.Pet;
import com.eternaltales.eternaltalesserver.global.mixin.TimestampMixin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Member extends TimestampMixin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 1024, unique = true, updatable = false)
	private String username;

	@Column(nullable = false, length = 255)
	private String name;

	@OneToMany(mappedBy = "member")
	private List<Pet> pets = new ArrayList<>();
}

