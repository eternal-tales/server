package com.eternaltales.eternaltalesserver.domain.pethistory.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/pet/history")
@Tag(name = "pet history 관련 API", description = "pet history 관련 API")
public class PetHistoryController {
}
