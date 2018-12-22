package com.titaniumtemplar.discordbot.controller;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class RootController {

	@Inject
	CyberscapeService service;

	@RequestMapping("/")
	String index() {
		return "redirect:/profile";
	}

	@RequestMapping("/profile")
	String profile(Authentication auth, Model model) {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");
		String username = (String) principal.getAttributes().get("username");

		CharStats character;
		try {
			character = service.getCharacter(userId);
		} catch (NoSuchCharacterException ex) {
			character = service.createCharacter(userId, username);
		}

		character.setName(username);
		model.addAttribute("character", character);
		return "charsheet";
	}

	@RequestMapping("/profile/{uid}")
	String profile(@PathVariable String uid, Model model) {
		CharStats character = service.getCharacter(uid);
		model.addAttribute("readOnly", true);
		model.addAttribute("character", character);
		return "charsheet";
	}
}
