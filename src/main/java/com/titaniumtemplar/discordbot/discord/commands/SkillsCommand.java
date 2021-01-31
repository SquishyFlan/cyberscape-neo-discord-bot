package com.titaniumtemplar.discordbot.discord.commands;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/*
	Class: SkillsCommand
	Description: Command object for skills
*/
@RequiredArgsConstructor(staticName = "withArgs")
public class SkillsCommand implements DiscordCommand {

  private final List<String> splitCommand;

	/*
		Method: run
		Description: handles skills
		Input: CyberscapeService object, Myra object, Message object, User object, Member object
	*/
  @Override
  public void run(CyberscapeService service, Myra myra, Message message, User author, Member member) {
    // TODO
  }

}
