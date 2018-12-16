package com.titaniumtemplar.discordbot.discord.commands;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@RequiredArgsConstructor(staticName = "withArgs")
public class SkillsCommand implements DiscordCommand {

  private final String[] splitCommand;

  @Override
  public void run(Message message, User author, Member member) {
    // TODO
  }

}
