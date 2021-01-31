package com.titaniumtemplar.discordbot.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.model.character.CharSkillsUpdate;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/api")
/*
	Class: AdminApiController
	Description: Establishes a baseline for Admin API Access
*/
public class AdminApiController {

	@Inject
	Myra myra;

	@Inject
	CyberscapeService service;

	/*
		Method: forceCombat
		Description: Initiate combat in channel
		Input: Authentication object, 2D Map GID object
	*/
	@PostMapping("forceCombat")
	void forceCombat(Authentication auth, @RequestBody Map<String, String> gidObj) {
		String gid = gidObj.values().stream().findFirst().get();
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		User user = myra.getUser(userId);
		Map<String, Guild> adminGuilds = myra.getAdminGuilds(user);
		if (!adminGuilds.containsKey(gid)) {
			throw new AccessDeniedException("User " + user.getName() + " is not an administrator of this guild.");
		}
		myra.forceCombat(gid);
	}

	/*
		Method: checkCharStats
		Description: Evaluates Character's stats
		Input: Character Skills Update object, Authentication object
		Output: Evaluation of character's skill
	*/
	@PostMapping("statCheck")
	public CharStats checkCharStats(@RequestBody CharSkillsUpdate charSkills, Authentication auth) {
		checkAnyGuildAdmin(auth);

		var uid = Optional.ofNullable(charSkills.getUserId())
			.filter(StringUtils::hasText)
			.orElse("~template~");

		return service.checkStats(uid, charSkills, true);
	}

	/*
		Method: loadCharacter
		Description: Loads character
		Input: UID String, Authentication object
		Output: Character Stats object
	*/
	@GetMapping("character/{uid}")
	public CharStats loadCharacter(@PathVariable String uid, Authentication auth) {
		checkAnyGuildAdmin(auth);

		try {
			return service.getCharacter(uid);
		} catch (NoSuchCharacterException ex) {
			return service.getCharacter("~template~");
		}
	}

	/*
		Method: updateCharacter
		Description: Updates the given character
		Input: Character Skills Update object, Authentication object
	*/
	@PutMapping("character")
	@ResponseStatus(NO_CONTENT)
	public void updateCharacter(@RequestBody CharSkillsUpdate charSkills, Authentication auth) {
		checkAnyGuildAdmin(auth);

		var uid = Optional.ofNullable(charSkills.getUserId())
			.filter(StringUtils::hasText)
			.orElseThrow(() -> new IllegalArgumentException("User ID required for saving template!"));

		service.updateCharSkills(uid.toLowerCase(), charSkills, true);
	}

	/*
		Method: checkAnyGuildAdmin
		Description: Evaulate if user is a Guild Admin
		Input: Authentication object
			*/
	private void checkAnyGuildAdmin(Authentication auth) throws AccessDeniedException {
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");
		User user = myra.getUser(userId);
		Map<String, Guild> adminGuilds = myra.getAdminGuilds(user);
		if (adminGuilds.isEmpty()) {
			throw new AccessDeniedException("User " + user.getName() + " is not an administrator of any guild.");
		}
	}
}
