package com.titaniumtemplar.discordbot.discord.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public interface DiscordCommand {
  void run(Message message, User author, Member member);
}
