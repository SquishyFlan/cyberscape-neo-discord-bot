package com.titaniumtemplar.discordbot.discord;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/*
	Class: GuildSettings
	Description: Parent class for Guilds
*/
@Data
@Builder
public class GuildSettings {
  String defaultRoleId;

  @Singular
  Set<String> combatChannels;
}
