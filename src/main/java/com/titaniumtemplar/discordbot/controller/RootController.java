package com.titaniumtemplar.discordbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/*
	Class: RootController
	Description: Controls the game and establishes a baseline of functionality for normal users.	
*/
@Slf4j
@Controller
@RequestMapping("/")
public class RootController {

	@Inject
	CyberscapeService service;

	@Inject
	Myra myra;

	@Inject
	ObjectMapper objectMapper;

	@Inject
	ServletContext servletContext;

	/*
		Method: index
		Description: Returns the root level
		Output: String containing index location
	*/
	@GetMapping("")
	String index() {
		return "redirect:/profile/";
	}

	/*
		Method: profile
		Description: Profile constructor
		Input: Authentication object, model pertaining to user type
		Output: String containing success message
	*/
	@GetMapping("profile")
	String profile(Authentication auth, Model model) throws Exception {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");
		String username = (String) principal.getAttributes().get("username");

		CharStats character;
		try {
			character = service.getCharacter(userId);
		} catch (NoSuchCharacterException ex) {
			myra.register(userId);
			character = service.getCharacter(userId);
		}

		character.setName(username);
		model.addAttribute("character", character);
		model.addAttribute("charJson", objectMapper.writeValueAsString(character));
		model.addAttribute("urlPrefix", servletContext.getContextPath());
		model.addAttribute("admin", false);
		model.addAttribute("statConfig", service.getStatConfig());
		return "charsheet";
	}

	/*
		Method: profile
		Description: Profile Constructor
		Input: UID string, model pertaining to user type
		Output: String containing success message
	*/
	@GetMapping("profile/{uid}")
	String profile(@PathVariable String uid, Model model) {
		CharStats character = service.getCharacter(uid);
		model.addAttribute("readOnly", true);
		model.addAttribute("admin", false);
		model.addAttribute("character", character);
		return "charsheet";
	}
}
