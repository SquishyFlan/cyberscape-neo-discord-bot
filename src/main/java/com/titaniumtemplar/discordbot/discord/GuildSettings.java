package com.titaniumtemplar.discordbot.discord;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class GuildSettings {
  String defaultRoleId;

  @Singular
  Set<String> combatChannels;
}
