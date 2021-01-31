package com.titaniumtemplar.discordbot.discord.commands;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/*
	Class: DiscordUtils
	Description: Utility functions for Discord operations
*/
@RequiredArgsConstructor(staticName = "withArgs")
public class UnknownCommand implements DiscordCommand {

  private final List<String> splitCommand;

	/*
		Method: run
		Description: handles unknown commands (by doing nothing)
		Input: CyberscapeService object, Myra object, Message object, User object, Member object
	*/
  @Override
  public void run(CyberscapeService service, Myra myra, Message message, User author, Member member) {
    // no-op
  }

}
