package com.eternaltales.eternaltalesserver.domain.member.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/member")
@Tag(name = "member 관련 API", description = "member 관련 API")
public class MemberController {

}
