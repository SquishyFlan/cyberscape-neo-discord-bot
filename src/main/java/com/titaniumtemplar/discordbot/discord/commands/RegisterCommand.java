package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.discord.DiscordUtils.deleteMessage;
import static com.titaniumtemplar.discordbot.discord.DiscordUtils.sendDm;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/*
	Class: RegisterCommand
	Description: Class that handles registering a user
*/
@RequiredArgsConstructor(staticName = "withArgs")
public class RegisterCommand implements DiscordCommand {

	private final List<String> splitCommand;

	/*
		Method: run
		Description: gets users registered
		Input: CyberscapeService object, Myra object, Message object, User object, Member object
	*/
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

			if (member == null) {
				member = myra.getMember(author);
				if (member == null) {
					sendDm(author, "I'm sorry, you don't seem to be connected to any Cyberscape Neo compatible server!");
					return;
				}
			}

			service.createCharacter(author.getId(), member.getEffectiveName());
			sendDm(author, "Thank you for registering your account! Check your profile at <" + myra.getBaseUrl() + "profile/>!");

			Guild guild = member.getGuild();
			guild
				.getController()
				.modifyMemberRoles(
					member,
					guild.getRolesByName("In Character Select", false),
					guild.getRolesByName("Playing Cyberscape Neo", false))
				.queue();

		} finally {
			deleteMessage(message);
		}
	}

}
