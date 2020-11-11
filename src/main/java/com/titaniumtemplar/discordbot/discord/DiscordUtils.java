package com.titaniumtemplar.discordbot.discord;

import static net.dv8tion.jda.api.entities.ChannelType.TEXT;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

@UtilityClass
public class DiscordUtils {

  public static void sendDm(User user, String msg) {
    user.openPrivateChannel()
	.queue((channel) -> channel.sendMessage(msg).queue());
  }

  public static void deleteMessage(Message message) {
    if (message != null && message.getChannelType() == TEXT) {
      message.delete().queue();
    }
  }
}
