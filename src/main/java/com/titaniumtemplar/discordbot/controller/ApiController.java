package com.titaniumtemplar.discordbot.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.titaniumtemplar.discordbot.model.character.CharSkillsUpdate;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api")
public class ApiController {

	@Inject
	CyberscapeService service;

	@GetMapping("character")
	public CharStats getCharStats(Authentication auth) {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		return service.getCharacter(userId);
	}

	@PutMapping("character")
	@ResponseStatus(NO_CONTENT)
	public void updateCharacter(@RequestBody CharSkillsUpdate charSkills, Authentication auth) {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		service.updateCharSkills(userId, charSkills, false);
	}

	@PostMapping("statCheck")
	public CharStats checkCharStats(@RequestBody CharSkillsUpdate charSkills, Authentication auth) {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		return service.checkStats(userId, charSkills, false);
	}
}
