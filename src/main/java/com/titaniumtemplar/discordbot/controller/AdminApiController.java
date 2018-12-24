package com.titaniumtemplar.discordbot.controller;

import com.titaniumtemplar.discordbot.discord.Myra;
import java.util.Map;
import javax.inject.Inject;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/api")
public class AdminApiController {

	@Inject
	Myra myra;

	@PostMapping("forceCombat")
	void forceCombat(Authentication auth, @RequestBody Map<String, String> gidObj) {
		String gid = gidObj.values().stream().findFirst().get();
		OAuth2User principal = (OAuth2User) auth.getPrincipal();
		String userId = (String) principal.getAttributes().get("id");

		User user = myra.getUser(userId);
		Map<String, Guild> adminGuilds = myra.getAdminGuilds(user);
		if (!adminGuilds.containsKey(gid)) {
			throw new AccessDeniedException("User " + user.getName() + " is not an administrator.");
		}
		myra.forceCombat(gid);
	}
}
