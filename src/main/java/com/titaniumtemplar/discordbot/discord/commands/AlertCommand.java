package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.discord.DiscordUtils.deleteMessage;
import static com.titaniumtemplar.discordbot.discord.DiscordUtils.sendDm;
import static java.lang.Integer.parseInt;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

@RequiredArgsConstructor(staticName = "withArgs")
public class AlertCommand implements DiscordCommand {

	private final List<String> splitCommand;

	@Override
	public void run(
		CyberscapeService service,
		Myra myra,
		Message message,
		User author,
		Member member) {

		try {
			if (member == null) {
				member = myra.getMember(author);
				if (member == null) {
					sendDm(author, "I'm sorry, you don't seem to be connected to any Cyberscape Neo compatible server!");
					return;
				}
			}

			if (splitCommand.size() < 2) {
					sendDm(author, "You need to specify a number of hours to be alerted! Try \".alert 8\", for example.");
					return;
			}

			try {
				int numHours = parseInt(splitCommand.get(1));
				Guild guild = member.getGuild();
				Role grindingRole = guild.getRolesByName("Grinding for XP", true)
					.stream()
					.findAny()
					.orElseThrow(() -> new RuntimeException("Server is missing a Grinding For XP role!"));

				if (numHours < 1) {
					sendDm(author, "You will no longer be alerted for new combat.");

					guild
						.getController()
						.removeSingleRoleFromMember(member, grindingRole)
						.queue();

					return;
				}

				guild
					.getController()
					.addSingleRoleToMember(member, grindingRole)
					.queue();
				myra.scheduleGrinding(member, numHours);

				sendDm(author, "You will receive alerts for every combat for the next " + numHours + " hours. Happy grinding!");

			} catch (NumberFormatException ex) {
					sendDm(author, "I'm sorry, I didn't understand how many hours you wanted me to alert you for!");
					return;
			}
		} finally {
			deleteMessage(message);
		}
	}

}
