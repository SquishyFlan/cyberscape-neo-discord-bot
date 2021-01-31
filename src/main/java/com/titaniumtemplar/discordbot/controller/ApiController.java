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
/*
	Class: ApiController
	Description: Creates a baseline for normal users accessing the API
*/
public class ApiController {

	@Inject
	CyberscapeService service;

	/*
		Method: getCharStats
		Description: Retrieves current user's stats
		Input: Authentication object
		Output: Returns provided character's stats
	*/
	@GetMapping("character")
	public CharStats getCharStats(Authentication auth) {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		return service.getCharacter(userId);
	}

	/*
		Method: updateCharacter
		Description: updates provided character's skills
		Input: CharSkillsUpdate object for provided character, Authentication object
		Output: String containing success message
	*/
	@PutMapping("character")
	@ResponseStatus(NO_CONTENT)
	public void updateCharacter(@RequestBody CharSkillsUpdate charSkills, Authentication auth) {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		service.updateCharSkills(userId, charSkills, false);
	}
	/*
		Method: checkCharStats
		Description: Evaluate's character's stats change
		Input: CharSkillsUpdate object with changes, Authentication object,
		Output: Returns stats check evaluation
	*/
	@PostMapping("statCheck")
	public CharStats checkCharStats(@RequestBody CharSkillsUpdate charSkills, Authentication auth) {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		return service.checkStats(userId, charSkills, false);
	}
}
