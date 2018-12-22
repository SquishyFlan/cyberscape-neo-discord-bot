package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.discord.DiscordUtils.deleteMessage;
import static com.titaniumtemplar.discordbot.discord.DiscordUtils.sendDm;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@RequiredArgsConstructor(staticName = "withArgs")
public class RegisterCommand implements DiscordCommand {

	private final List<String> splitCommand;

	@Override
	public void run(
		CyberscapeService service,
		Myra myra,
		Message message,
		User author,
		Member member) {
		try {
			try {
				service.getCharacter(author.getId());
				sendDm(author, "You're already registered! Check your profile at <" + myra.getBaseUrl() + "profile/>!");
				return;
			} catch (NoSuchCharacterException ex) {
				// Expected
			}

			service.createCharacter(author.getId(), member.getEffectiveName());
			sendDm(author, "Thank you for registering your account! Check your profile at <" + myra.getBaseUrl() + "profile/>!");

		} finally {
			deleteMessage(message);
		}
	}

}
