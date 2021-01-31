package com.titaniumtemplar.discordbot.discord;

import static net.dv8tion.jda.core.entities.ChannelType.TEXT;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/*
	Class: DiscordUtils
	Description: Utility functions for Discord operations
*/
@UtilityClass
public class DiscordUtils {

	/*
		Method: sendDm
		Description: Sends a DM to a user
		Input: User Object, String Message
	*/
  public static void sendDm(User user, String msg) {
    user.openPrivateChannel()
	.queue((channel) -> channel.sendMessage(msg).queue());
  }

	/*
		Method: deleteMessage
		Description: Removes message from Discord
		Input: Message object
	*/
  public static void deleteMessage(Message message) {
    if (message != null && message.getChannelType() == TEXT) {
      message.delete().queue();
    }
  }
}
