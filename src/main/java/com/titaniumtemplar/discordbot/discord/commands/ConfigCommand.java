package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.discord.DiscordUtils.deleteMessage;
import static com.titaniumtemplar.discordbot.discord.DiscordUtils.sendDm;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

@Slf4j
@RequiredArgsConstructor(staticName = "withArgs")
public class ConfigCommand implements DiscordCommand {

	private final List<String> splitCommand;

	@Override
	public void run(
		CyberscapeService service,
		Myra myra,
		Message message,
		User author,
		Member member) {

		log.info("Config command: {}", splitCommand);

		try {
			if (member == null) {
				member = myra.getMember(author);
				if (member == null) {
					sendDm(author, "I'm sorry, you don't seem to be connected to any Cyberscape Neo compatible server!");
					return;
				}
			}

			if (!myra.isAdmin(member)) {
				sendDm(author, "I'm sorry, but only admins are permitted to configure me.");
				return;
			} else if (splitCommand.size() < 3) {
				// TODO: Better help text
				sendDm(author, "I'm sorry, I don't understand what you'd like me to configure.");
				return;
			}

			switch (splitCommand.get(1)) {
				case "combatChannel":
				case "combatChannels":
					String guildId = member.getGuild().getId();
					boolean add = splitCommand.get(2).equals("add");
					StringJoiner nameBuilder = new StringJoiner(" ");
					List<TextChannel> channels = message.getMentionedChannels();
					channels
						.stream()
						.filter((channel) -> guildId.equals(channel.getGuild().getId()))
						.peek((channel) -> nameBuilder.add(channel.getName()))
						.map(TextChannel::getId)
						.forEach((channelId) -> service.addCombatChannel(guildId, channelId, add));

					sendDm(author, "Combat channels [" + nameBuilder.toString() + "] " + (add ? "added." : "removed."));
					break;
				default:
					sendDm(author, "I'm sorry, I don't understand what you'd like me to configure.");
			}

		} finally {
			deleteMessage(message);
		}
	}

}
