package com.titaniumtemplar.discordbot.discord.commands;

import static java.util.stream.Collectors.joining;
import static net.dv8tion.jda.api.entities.ChannelType.TEXT;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/*
	Class: HelpCommand
	Description: Command that Helps users
*/
@RequiredArgsConstructor(staticName = "withArgs")
public class HelpCommand implements DiscordCommand {

	private final List<String> splitCommand;

	private static final String DEFAULT_HELP_TEXT = Stream.of("Hi there! I'm Myra, and welcome to Cyberscape Neo!",
		"To get started, you'll first need to register an account by typing \".register\"",
		"To view your character's profile, use \".profile\"",
		"You'll want to spend your skill points! Check and use them with \".skill\"",
		"Finally, you'll want to combat enemies to progress. Use \".attack\" to show your physical might!",
		"Or to stab from a distance, use \".shoot\"",
		"Feeling fancy? Flex your magical muscle by attacking with \".bolt\"!",
		"Type \".help <command>\" for help with a specific command; for example, \".help .attack\"",
		"Feel free to contact our support representatives on Discord if you have any further questions, and thank you for playing Cyberscape Neo!")
		.collect(joining("\n"));

	/*
		Method: run
		Description: Function that executes when Help Command is ran
		Input: CyberscapeService object, Myra objcet, Message object, User object, Member objcet
	*/
	@Override
	public void run(CyberscapeService service, Myra myra, Message message, User author, Member member) {
		author.openPrivateChannel()
			.queue((privateChat) -> {
				privateChat.sendMessage(DEFAULT_HELP_TEXT)
					.queue();
			});
		if (message.isFromType(TEXT)) {
			message.delete()
				.queue();
		}
	}

}
