package com.titaniumtemplar.discordbot.discord.commands;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@RequiredArgsConstructor(staticName = "withArgs")
public class UnknownCommand implements DiscordCommand {

  private final List<String> splitCommand;

  @Override
  public void run(CyberscapeService service, Myra myra, Message message, User author, Member member) {
    // no-op
  }

}
