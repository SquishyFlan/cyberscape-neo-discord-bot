package com.titaniumtemplar.discordbot.model.combat;

import com.titaniumtemplar.discordbot.model.monster.Monster;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

@Data
@NoArgsConstructor
public class Combat
{
  private UUID id;
  private Guild guild;
  private Monster monster;
  private Message message;
  private Set<UUID> participants;
  private Round previousRound;
  private Round currentRound;

  private Combat(UUID id) {
    this.id = id;
  }

  public static Combat valueOf(UUID id) {
    return new Combat(id);
  }

  public String resolveRound() {
    previousRound = currentRound;
    currentRound = new Round();
    return previousRound.resolve(monster);
  }
}
