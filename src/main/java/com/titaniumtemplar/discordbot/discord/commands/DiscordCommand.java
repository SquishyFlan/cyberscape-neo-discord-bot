package com.titaniumtemplar.discordbot.discord.commands;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/*
	Interface: DiscordCommand
	Description: Base class for all Discord Commands
*/
public interface DiscordCommand {
	/*
		Method: run
		Description: function that runs when object is called
		Input: CyberscapeService object, Myra object, Message object, User object, Member object
	*/
  void run(CyberscapeService service, Myra myra, Message message, User author, Member member);
}
